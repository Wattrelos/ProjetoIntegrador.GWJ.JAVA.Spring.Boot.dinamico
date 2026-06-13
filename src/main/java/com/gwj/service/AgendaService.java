package com.gwj.service;

import com.gwj.model.domain.entities.Agenda;
import com.gwj.model.domain.entities.Cliente;
import com.gwj.model.domain.entities.Profissional;
import com.gwj.model.domain.entities.Servico;
import com.gwj.service.transaction.UnitOfWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class AgendaService extends GenericService<Agenda> {

    private static final List<String> TODOS_HORARIOS = Arrays.asList(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
        "16:00", "16:30", "17:00", "17:30"
    );

    public AgendaService() {
        super(Agenda.class);
    }

    public List<Map<String, Object>> getHorariosDisponiveis(Long servicoId, Long profissionalId, String dataStr) {
        List<Map<String, Object>> slots = new ArrayList<>();
        try (UnitOfWork uow = new UnitOfWork()) {
            Connection conn = UnitOfWork.getConnection();
            LocalDate localDate = LocalDate.parse(dataStr);

            // 1. Obter profissionais ativos a considerar
            List<Profissional> profissionais = new ArrayList<>();
            if (profissionalId != null && profissionalId > 0) {
                IService<Profissional> profService = ServiceRegistry.getService("Profissional");
                Profissional filtro = new Profissional();
                filtro.setId(profissionalId);
                List<Profissional> res = profService.read(filtro);
                if (!res.isEmpty()) {
                    profissionais.addAll(res);
                }
            } else {
                IService<Profissional> profService = ServiceRegistry.getService("Profissional");
                Profissional filtro = new Profissional();
                filtro.setStatus(true); // Apenas profissionais ativos
                profissionais.addAll(profService.read(filtro));
            }

            if (profissionais.isEmpty()) {
                // Se não há nenhum profissional configurado/ativo, nenhum slot estará livre
                for (String h : TODOS_HORARIOS) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("horario", h);
                    map.put("disponivel", false);
                    slots.add(map);
                }
                return slots;
            }

            // 2. Buscar agendamentos existentes na data para estes profissionais
            String sql = "SELECT data, profissional_id FROM tab_agenda WHERE DATE(data) = ?";
            Map<Long, Set<String>> ocupadosPorProfissional = new HashMap<>();
            for (Profissional p : profissionais) {
                ocupadosPorProfissional.put(p.getId(), new HashSet<>());
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, localDate.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Timestamp ts = rs.getTimestamp("data");
                        long profId = rs.getLong("profissional_id");
                        if (ts != null) {
                            LocalDateTime dt = ts.toLocalDateTime();
                            String hhMm = String.format("%02d:%02d", dt.getHour(), dt.getMinute());
                            if (ocupadosPorProfissional.containsKey(profId)) {
                                ocupadosPorProfissional.get(profId).add(hhMm);
                            }
                        }
                    }
                }
            }

            // 3. Montar a resposta com base nos horários cadastrados
            for (String h : TODOS_HORARIOS) {
                boolean disponivel = false;
                if (profissionalId != null && profissionalId > 0) {
                    // Profissional específico
                    Set<String> ocupados = ocupadosPorProfissional.get(profissionalId);
                    disponivel = (ocupados != null && !ocupados.contains(h));
                } else {
                    // Qualquer profissional livre: basta que pelo menos um não esteja ocupado
                    for (Profissional p : profissionais) {
                        Set<String> ocupados = ocupadosPorProfissional.get(p.getId());
                        if (ocupados != null && !ocupados.contains(h)) {
                            disponivel = true;
                            break;
                        }
                    }
                }

                Map<String, Object> map = new HashMap<>();
                map.put("horario", h);
                map.put("disponivel", disponivel);
                slots.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao calcular disponibilidade de horários", e);
        }
        return slots;
    }

    public Agenda confirmarReserva(Long servicoId, Long profissionalId, String dataHoraStr,
                                   String nome, String sobrenome, String email, String telefone) {
        
        // 1. Obter ou criar o Cliente de forma transacional
        Cliente cliente = obterOuCriarCliente(nome, sobrenome, email, telefone);

        // 2. Carregar o Serviço selecionado
        IService<Servico> servicoService = ServiceRegistry.getService("Servico");
        Servico sFiltro = new Servico();
        sFiltro.setId(servicoId);
        List<Servico> servicos = servicoService.read(sFiltro);
        if (servicos.isEmpty()) {
            throw new RuntimeException("O serviço selecionado não foi encontrado.");
        }
        Servico servico = servicos.get(0);

        // 3. Converter a data e hora selecionada
        LocalDateTime dataHora = LocalDateTime.parse(dataHoraStr); // formato ISO-8601: yyyy-MM-ddTHH:mm:ss

        // 4. Determinar qual profissional fará o atendimento
        Profissional profissional = determinarProfissional(profissionalId, dataHora);
        if (profissional == null) {
            throw new RuntimeException("Desculpe, o profissional selecionado não está mais disponível para este horário.");
        }

        // 5. Montar a Agenda
        Agenda agenda = new Agenda();
        agenda.setNome("Agendamento - " + servico.getNome());
        agenda.setData(dataHora);
        agenda.setCliente(cliente);
        agenda.setProfissional(profissional);
        
        // Inicializa a lista e adiciona o serviço
        List<Servico> listaServico = new ArrayList<>();
        listaServico.add(servico);
        agenda.setListaServico(listaServico);

        // 6. Criar o agendamento através do GenericService/UnitOfWork
        return this.create(agenda);
    }

    private Cliente obterOuCriarCliente(String nome, String sobrenome, String email, String telefone) {
        IService<Cliente> clienteService = ServiceRegistry.getService("Cliente");
        
        // Busca cliente por email
        Cliente filtro = new Cliente();
        filtro.setEmail(email);
        List<Cliente> clientes = clienteService.read(filtro);
        for (Cliente c : clientes) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }

        // Criar novo cliente
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setSobrenome(sobrenome);
        novoCliente.setEmail(email);
        novoCliente.setTelefone(telefone);
        novoCliente.setNomeUsuario(email);
        novoCliente.setSenha("123"); // Senha padrão guest
        novoCliente.setStatus(true);

        return clienteService.create(novoCliente);
    }

    private Profissional determinarProfissional(Long profissionalId, LocalDateTime dataHora) {
        IService<Profissional> profService = ServiceRegistry.getService("Profissional");
        
        if (profissionalId != null && profissionalId > 0) {
            // Verifica se o profissional específico está ocupado
            if (isProfissionalOcupado(profissionalId, dataHora)) {
                return null;
            }
            Profissional filtro = new Profissional();
            filtro.setId(profissionalId);
            List<Profissional> res = profService.read(filtro);
            return res.isEmpty() ? null : res.get(0);
        } else {
            // Qualquer profissional livre: pega a lista de todos e retorna o primeiro livre
            Profissional filtro = new Profissional();
            filtro.setStatus(true);
            List<Profissional> profissionais = profService.read(filtro);
            for (Profissional p : profissionais) {
                if (!isProfissionalOcupado(p.getId(), dataHora)) {
                    return p;
                }
            }
        }
        return null;
    }

    private boolean isProfissionalOcupado(Long profId, LocalDateTime dataHora) {
        String sql = "SELECT COUNT(*) FROM tab_agenda WHERE profissional_id = ? AND data = ?";
        try (UnitOfWork uow = new UnitOfWork()) {
            Connection conn = UnitOfWork.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, profId);
                stmt.setTimestamp(2, java.sql.Timestamp.valueOf(dataHora));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
