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
import java.time.LocalTime;
import java.util.*;

public class AgendaService extends GenericService<Agenda> {

    public AgendaService() {
        super(Agenda.class);
    }

    public List<Map<String, Object>> getHorariosDisponiveis(Long servicoId, Long profissionalId, String dataStr) {
        List<Map<String, Object>> slots = new ArrayList<>();
        try (UnitOfWork uow = new UnitOfWork()) {
            Connection conn = UnitOfWork.getConnection();
            LocalDate localDate = LocalDate.parse(dataStr);
            int diaSemana = localDate.getDayOfWeek().getValue() % 7 + 1; // 1=Domingo, 2=Segunda, etc.

            // 1. Obter funcionamento
            boolean aberto = false;
            long diaFuncionamentoId = 0;
            LocalTime diaInicio = null;
            LocalTime diaFim = null;

            String sqlDias = "SELECT id, aberto, horario_inicio, horario_fim FROM tab_dias_funcionamento WHERE dia_semana = ?";
            try (PreparedStatement stmtDias = conn.prepareStatement(sqlDias)) {
                stmtDias.setInt(1, diaSemana);
                try (ResultSet rsDias = stmtDias.executeQuery()) {
                    if (rsDias.next()) {
                        aberto = rsDias.getBoolean("aberto");
                        diaFuncionamentoId = rsDias.getLong("id");
                        java.sql.Time hiVal = rsDias.getTime("horario_inicio");
                        java.sql.Time hfVal = rsDias.getTime("horario_fim");
                        if (hiVal != null) diaInicio = hiVal.toLocalTime();
                        if (hfVal != null) diaFim = hfVal.toLocalTime();
                    }
                }
            }

            if (!aberto || diaFuncionamentoId == 0 || diaInicio == null || diaFim == null) {
                return slots;
            }

            // 2. Obter a grade de horários
            List<LocalTime> startTimes = new ArrayList<>();
            String sqlGrade = "SELECT horario_inicio FROM tab_grade_horarios WHERE dia_funcionamento_id = ? ORDER BY horario_inicio ASC";
            try (PreparedStatement stmtGrade = conn.prepareStatement(sqlGrade)) {
                stmtGrade.setLong(1, diaFuncionamentoId);
                try (ResultSet rsGrade = stmtGrade.executeQuery()) {
                    while (rsGrade.next()) {
                        java.sql.Time t = rsGrade.getTime("horario_inicio");
                        if (t != null) {
                            startTimes.add(t.toLocalTime());
                        }
                    }
                }
            }

            int duracao = 30;
            if (servicoId != null && servicoId > 0) {
                IService<Servico> servicoService = ServiceRegistry.getService("Servico");
                Servico sFiltro = new Servico();
                sFiltro.setId(servicoId);
                List<Servico> servicos = servicoService.read(sFiltro);
                if (!servicos.isEmpty()) {
                    duracao = servicos.get(0).getDuracao();
                }
            }

            List<Profissional> profissionais = new ArrayList<>();
            IService<Profissional> profService = ServiceRegistry.getService("Profissional");
            if (profissionalId != null && profissionalId > 0) {
                Profissional filtro = new Profissional();
                filtro.setId(profissionalId);
                profissionais.addAll(profService.read(filtro));
            } else {
                Profissional filtro = new Profissional();
                filtro.setStatus(true);
                profissionais.addAll(profService.read(filtro));
            }

            if (startTimes.isEmpty() || profissionais.isEmpty()) {
                return slots;
            }

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
                            LocalTime time = ts.toLocalDateTime().toLocalTime();
                            String hhMm = String.format("%02d:%02d", time.getHour(), time.getMinute());
                            if (ocupadosPorProfissional.containsKey(profId)) {
                                ocupadosPorProfissional.get(profId).add(hhMm);
                            }
                        }
                    }
                }
            }

            LocalDate hoje = LocalDate.now();
            LocalTime agora = LocalTime.now();

            for (LocalTime slotStart : startTimes) {
                String h = String.format("%02d:%02d", slotStart.getHour(), slotStart.getMinute());
                long startMin = slotStart.getHour() * 60L + slotStart.getMinute();
                long endMin = startMin + duracao;
                long closingMin = diaFim.getHour() * 60L + diaFim.getMinute();

                boolean disponivel = false;

                // Impedir horários do passado
                boolean noPassado = localDate.isBefore(hoje) || (localDate.isEqual(hoje) && slotStart.isBefore(agora));

                if (endMin <= closingMin && !noPassado) {
                    if (profissionalId != null && profissionalId > 0) {
                        Set<String> ocupados = ocupadosPorProfissional.get(profissionalId);
                        disponivel = (ocupados != null && !ocupados.contains(h));
                    } else {
                        for (Profissional p : profissionais) {
                            Set<String> ocupados = ocupadosPorProfissional.get(p.getId());
                            if (ocupados != null && !ocupados.contains(h)) {
                                disponivel = true;
                                break;
                            }
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
        Cliente cliente = obterOuCriarCliente(nome, sobrenome, email, telefone);
        IService<Servico> servicoService = ServiceRegistry.getService("Servico");
        Servico sFiltro = new Servico();
        sFiltro.setId(servicoId);
        List<Servico> servicos = servicoService.read(sFiltro);
        if (servicos.isEmpty()) throw new RuntimeException("Serviço não encontrado.");
        Servico servico = servicos.get(0);

        LocalDateTime dataHora = LocalDateTime.parse(dataHoraStr);
        LocalDate data = dataHora.toLocalDate();
        LocalTime horaInicio = dataHora.toLocalTime();
        LocalTime horaFim = horaInicio.plusMinutes(servico.getDuracao());

        // Regra para impedir agendamento no passado
        LocalDateTime agora = LocalDateTime.now();
        if (dataHora.isBefore(agora)) {
            throw new RuntimeException("Não é possível realizar um agendamento para uma data/hora no passado.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            Connection conn = UnitOfWork.getConnection();
            int diaSemana = data.getDayOfWeek().getValue() % 7 + 1;
            String sqlDias = "SELECT aberto, horario_inicio, horario_fim FROM tab_dias_funcionamento WHERE dia_semana = ?";
            try (PreparedStatement stmtDias = conn.prepareStatement(sqlDias)) {
                stmtDias.setInt(1, diaSemana);
                try (ResultSet rsDias = stmtDias.executeQuery()) {
                    if (rsDias.next()) {
                        if (!rsDias.getBoolean("aberto")) throw new RuntimeException("Barbearia fechada neste dia.");
                        LocalTime diaInicio = rsDias.getTime("horario_inicio").toLocalTime();
                        LocalTime diaFim = rsDias.getTime("horario_fim").toLocalTime();
                        if (horaInicio.isBefore(diaInicio) || horaFim.isAfter(diaFim)) throw new RuntimeException("Fora do horário de funcionamento.");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar horário: " + e.getMessage());
        }

        Profissional profissional = determinarProfissional(profissionalId, dataHora);
        if (profissional == null) throw new RuntimeException("Profissional não disponível.");

        Agenda agenda = new Agenda();
        agenda.setNome("Agendamento - " + servico.getNome());
        agenda.setData(dataHora);
        agenda.setCliente(cliente);
        agenda.setProfissional(profissional);
        agenda.setListaServico(Arrays.asList(servico));
        return this.create(agenda);
    }

    private Cliente obterOuCriarCliente(String nome, String sobrenome, String email, String telefone) {
        IService<Cliente> clienteService = ServiceRegistry.getService("Cliente");
        Cliente filtro = new Cliente();
        filtro.setEmail(email);
        List<Cliente> clientes = clienteService.read(filtro);
        for (Cliente c : clientes) if (c.getEmail().equalsIgnoreCase(email)) return c;
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setSobrenome(sobrenome);
        novoCliente.setEmail(email);
        novoCliente.setTelefone(telefone);
        novoCliente.setNomeUsuario(email);
        novoCliente.setSenha("123");
        novoCliente.setStatus(true);
        return clienteService.create(novoCliente);
    }

    private Profissional determinarProfissional(Long profissionalId, LocalDateTime dataHora) {
        IService<Profissional> profService = ServiceRegistry.getService("Profissional");
        if (profissionalId != null && profissionalId > 0) {
            if (isProfissionalOcupado(profissionalId, dataHora)) return null;
            Profissional filtro = new Profissional();
            filtro.setId(profissionalId);
            List<Profissional> res = profService.read(filtro);
            return res.isEmpty() ? null : res.get(0);
        } else {
            Profissional filtro = new Profissional();
            filtro.setStatus(true);
            for (Profissional p : profService.read(filtro)) {
                if (!isProfissionalOcupado(p.getId(), dataHora)) return p;
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
                    if (rs.next()) return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
