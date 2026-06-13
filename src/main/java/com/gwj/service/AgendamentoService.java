package com.gwj.service;
 
import com.gwj.model.domain.entities.Agendamento;
import com.gwj.model.domain.entities.Profissional;
import com.gwj.model.domain.entities.Servico;
import com.gwj.service.transaction.UnitOfWork;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
 
public class AgendamentoService extends GenericService<Agendamento> {
 
    private static final List<String> TODOS_HORARIOS = Arrays.asList(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
        "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
        "17:00", "17:30", "18:00", "18:30"
    );
 
    private static final LocalTime HORA_FECHAMENTO = LocalTime.of(19, 0);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
 
    public AgendamentoService() {
        super(Agendamento.class);
    }
 
    public List<Map<String, Object>> getHorariosDisponiveis(Long servicoId, Long profissionalId, String dataStr) {
        List<Map<String, Object>> slots = new ArrayList<>();

        // 1. Obter duração do serviço (fora do UnitOfWork de consulta de slots)
        int duracao = 30; // Duração padrão em minutos
        if (servicoId != null && servicoId > 0) {
            IService<Servico> servicoService = ServiceRegistry.getService("Servico");
            Servico sFiltro = new Servico();
            sFiltro.setId(servicoId);
            List<Servico> servicos = servicoService.read(sFiltro);
            if (!servicos.isEmpty()) {
                duracao = servicos.get(0).getDuracao();
            }
        }

        // 2. Obter profissionais ativos (fora do UnitOfWork de consulta de slots)
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
            for (String h : TODOS_HORARIOS) {
                Map<String, Object> map = new HashMap<>();
                map.put("horario", h);
                map.put("disponivel", false);
                slots.add(map);
            }
            return slots;
        }

        // Agora abre o UnitOfWork para a consulta SQL direta de agendamentos
        try (UnitOfWork uow = new UnitOfWork()) {
            Connection conn = UnitOfWork.getConnection();
            LocalDate localDate = LocalDate.parse(dataStr);

            // 3. Buscar agendamentos existentes na data para estes profissionais
            String sql = "SELECT hora_inicio, hora_fim, profissional_id FROM tab_agendamento WHERE data_agendamento = ? AND status = 'Confirmado'";
            Map<Long, List<BookingInterval>> agendamentosPorProf = new HashMap<>();
            for (Profissional p : profissionais) {
                agendamentosPorProf.put(p.getId(), new ArrayList<>());
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, localDate.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Time startVal = rs.getTime("hora_inicio");
                        java.sql.Time endVal = rs.getTime("hora_fim");
                        long profId = rs.getLong("profissional_id");

                        if (startVal != null && endVal != null) {
                            LocalTime start = startVal.toLocalTime();
                            LocalTime end = endVal.toLocalTime();
                            if (agendamentosPorProf.containsKey(profId)) {
                                agendamentosPorProf.get(profId).add(new BookingInterval(start, end));
                            }
                        }
                    }
                }
            }

            // 4. Montar a resposta validando fechamento e colisões
            for (String h : TODOS_HORARIOS) {
                LocalTime slotStart = LocalTime.parse(h);
                
                // Regra de Fechamento: H + D <= 19:00
                long startMin = slotStart.getHour() * 60L + slotStart.getMinute();
                long endMin = startMin + duracao;
                long closingMin = HORA_FECHAMENTO.getHour() * 60L + HORA_FECHAMENTO.getMinute();

                boolean disponivel = false;

                if (endMin <= closingMin) {
                    LocalTime slotEnd = slotStart.plusMinutes(duracao);

                    if (profissionalId != null && profissionalId > 0) {
                        // Profissional específico
                        List<BookingInterval> bookings = agendamentosPorProf.get(profissionalId);
                        disponivel = (bookings != null && !hasOverlap(slotStart, slotEnd, bookings));
                    } else {
                        // Qualquer profissional livre: pelo menos um livre
                        for (Profissional p : profissionais) {
                            List<BookingInterval> bookings = agendamentosPorProf.get(p.getId());
                            if (bookings != null && !hasOverlap(slotStart, slotEnd, bookings)) {
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
 
    private boolean hasOverlap(LocalTime slotStart, LocalTime slotEnd, List<BookingInterval> bookings) {
        for (BookingInterval booking : bookings) {
            if (slotStart.isBefore(booking.end) && booking.start.isBefore(slotEnd)) {
                return true;
            }
        }
        return false;
    }
 
    public Agendamento confirmarReserva(Long servicoId, Long profissionalId, String dataStr, String horaInicioStr,
                                        String nome, String sobrenome, String email, String telefone) {
 
        // 1. Carregar Serviço
        IService<Servico> servicoService = ServiceRegistry.getService("Servico");
        Servico sFiltro = new Servico();
        sFiltro.setId(servicoId);
        List<Servico> servicos = servicoService.read(sFiltro);
        if (servicos.isEmpty()) {
            throw new RuntimeException("O serviço selecionado não foi encontrado.");
        }
        Servico servico = servicos.get(0);
 
        // 2. Definir Horários
        LocalDate data = LocalDate.parse(dataStr);
        LocalTime horaInicio = LocalTime.parse(horaInicioStr);
        LocalTime horaFim = horaInicio.plusMinutes(servico.getDuracao());
 
        // Validar se excede as 19:00
        long endMin = horaFim.getHour() * 60L + horaFim.getMinute();
        long closingMin = HORA_FECHAMENTO.getHour() * 60L + HORA_FECHAMENTO.getMinute();
        if (endMin > closingMin || horaFim.isBefore(horaInicio)) {
            throw new RuntimeException("Desculpe, o horário do agendamento excede o limite de funcionamento da barbearia (19:00).");
        }
 
        // 3. Determinar Profissional
        Profissional profissional = determinarProfissionalLivre(profissionalId, data, horaInicio, horaFim);
        if (profissional == null) {
            throw new RuntimeException("Desculpe, não há nenhum profissional livre para o horário e duração selecionados.");
        }
 
        // 4. Montar e criar o Agendamento
        Agendamento agendamento = new Agendamento();
        agendamento.setClienteNome(nome + " " + sobrenome);
        agendamento.setClienteTelefone(telefone);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setDataAgendamento(data);
        agendamento.setHoraInicio(horaInicio);
        agendamento.setHoraFim(horaFim);
        agendamento.setStatus("Confirmado");
 
        return this.create(agendamento);
    }
 
    private Profissional determinarProfissionalLivre(Long profissionalId, LocalDate data, LocalTime inicio, LocalTime fim) {
        IService<Profissional> profService = ServiceRegistry.getService("Profissional");
        
        List<Profissional> candidatos = new ArrayList<>();
        if (profissionalId != null && profissionalId > 0) {
            Profissional filtro = new Profissional();
            filtro.setId(profissionalId);
            List<Profissional> res = profService.read(filtro);
            if (!res.isEmpty()) {
                candidatos.add(res.get(0));
            }
        } else {
            Profissional filtro = new Profissional();
            filtro.setStatus(true);
            candidatos.addAll(profService.read(filtro));
        }

        if (candidatos.isEmpty()) {
            return null;
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            Connection conn = UnitOfWork.getConnection();
            for (Profissional p : candidatos) {
                if (!isProfissionalOcupado(conn, p.getId(), data, inicio, fim)) {
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
 
    private boolean isProfissionalOcupado(Connection conn, Long profId, LocalDate data, LocalTime inicio, LocalTime fim) {
        String sql = "SELECT COUNT(*) FROM tab_agendamento WHERE profissional_id = ? AND data_agendamento = ? AND status = 'Confirmado' AND hora_inicio < ? AND ? < hora_fim";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, profId);
            stmt.setString(2, data.toString());
            stmt.setString(3, fim.format(TIME_FORMATTER));
            stmt.setString(4, inicio.format(TIME_FORMATTER));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
 
    private static class BookingInterval {
        final LocalTime start;
        final LocalTime end;
 
        BookingInterval(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
    }
}
