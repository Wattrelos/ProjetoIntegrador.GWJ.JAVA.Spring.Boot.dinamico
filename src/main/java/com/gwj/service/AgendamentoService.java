package com.gwj.service;
 
import com.gwj.model.domain.entities.Agendamento;
import com.gwj.model.domain.entities.DiasFuncionamento;
import com.gwj.model.domain.entities.GradeHorarios;
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
 
        // Agora abre o UnitOfWork para a consulta SQL
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
 
            // Se não funciona neste dia, retorna lista vazia
            if (!aberto || diaFuncionamentoId == 0 || diaInicio == null || diaFim == null) {
                return slots;
            }
 
            // 2. Obter a grade de horários cadastrada para esse dia
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
 
            if (startTimes.isEmpty() || profissionais.isEmpty()) {
                for (LocalTime st : startTimes) {
                    Map<String, Object> map = new HashMap<>();
                    String formatado = String.format("%02d:%02d", st.getHour(), st.getMinute());
                    map.put("horario", formatado);
                    map.put("disponivel", false);
                    slots.add(map);
                }
                return slots;
            }
 
            // 3. Buscar agendamentos existentes na data para estes profissionais
            String sqlAgendamentos = "SELECT hora_inicio, hora_fim, profissional_id FROM tab_agendamento WHERE data_agendamento = ? AND status = 'Confirmado'";
            Map<Long, List<BookingInterval>> agendamentosPorProf = new HashMap<>();
            for (Profissional p : profissionais) {
                agendamentosPorProf.put(p.getId(), new ArrayList<>());
            }
 
            try (PreparedStatement stmt = conn.prepareStatement(sqlAgendamentos)) {
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
            for (LocalTime slotStart : startTimes) {
                String hStr = String.format("%02d:%02d", slotStart.getHour(), slotStart.getMinute());
 
                // Regra de Fechamento: H + D <= diaFim
                long startMin = slotStart.getHour() * 60L + slotStart.getMinute();
                long endMin = startMin + duracao;
                long closingMin = diaFim.getHour() * 60L + diaFim.getMinute();
 
                boolean disponivel = false;
 
                if (endMin <= closingMin) {
                    LocalTime slotEnd = slotStart.plusMinutes(duracao);
 
                    if (profissionalId != null && profissionalId > 0) {
                        List<BookingInterval> bookings = agendamentosPorProf.get(profissionalId);
                        disponivel = (bookings != null && !hasOverlap(slotStart, slotEnd, bookings));
                    } else {
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
                map.put("horario", hStr);
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
 
        // 2. Definir Horários e obter limites e slot da grade
        LocalDate data = LocalDate.parse(dataStr);
        LocalTime horaInicio = LocalTime.parse(horaInicioStr);
        LocalTime horaFim = horaInicio.plusMinutes(servico.getDuracao());
 
        GradeHorarios gradeHorarios = null;
        LocalTime diaInicio = null;
        LocalTime diaFim = null;
        boolean aberto = false;
 
        try (UnitOfWork uow = new UnitOfWork()) {
            Connection conn = UnitOfWork.getConnection();
            int diaSemana = data.getDayOfWeek().getValue() % 7 + 1;
 
            // 1. Obter funcionamento e limites
            String sqlDias = "SELECT id, aberto, horario_inicio, horario_fim FROM tab_dias_funcionamento WHERE dia_semana = ?";
            long diaFuncionamentoId = 0;
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
                throw new RuntimeException("Desculpe, a barbearia não funciona neste dia da semana.");
            }
 
            // 2. Validar limites de abertura e fechamento
            long startMin = horaInicio.getHour() * 60L + horaInicio.getMinute();
            long endMin = horaFim.getHour() * 60L + horaFim.getMinute();
            long openMin = diaInicio.getHour() * 60L + diaInicio.getMinute();
            long closingMin = diaFim.getHour() * 60L + diaFim.getMinute();
 
            if (startMin < openMin || endMin > closingMin || horaFim.isBefore(horaInicio)) {
                String limiteStr = diaFim.toString().substring(0, 5);
                throw new RuntimeException("Desculpe, o horário do agendamento excede o limite de funcionamento da barbearia (" + limiteStr + ").");
            }
 
            // 3. Obter o slot da grade correspondente ao horário de início e dia de funcionamento
            String sqlGrade = "SELECT id, horario_inicio, horario_fim FROM tab_grade_horarios WHERE dia_funcionamento_id = ? AND horario_inicio = ?";
            try (PreparedStatement stmtGrade = conn.prepareStatement(sqlGrade)) {
                stmtGrade.setLong(1, diaFuncionamentoId);
                stmtGrade.setString(2, horaInicio.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                try (ResultSet rsGrade = stmtGrade.executeQuery()) {
                    if (rsGrade.next()) {
                        gradeHorarios = new GradeHorarios();
                        gradeHorarios.setId(rsGrade.getLong("id"));
                        gradeHorarios.setHorarioInicio(rsGrade.getTime("horario_inicio").toLocalTime());
                        gradeHorarios.setHorarioFim(rsGrade.getTime("horario_fim").toLocalTime());
 
                        DiasFuncionamento df = new DiasFuncionamento();
                        df.setId(diaFuncionamentoId);
                        df.setDiaSemana(diaSemana);
                        df.setAberto(aberto);
                        df.setHorarioInicio(diaInicio);
                        df.setHorarioFim(diaFim);
                        gradeHorarios.setDiaFuncionamento(df);
                    }
                }
            }
 
            if (gradeHorarios == null) {
                throw new RuntimeException("Desculpe, o horário inicial selecionado não é um slot válido na grade de funcionamento da barbearia.");
            }
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao processar as configurações de agendamento", e);
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
        agendamento.setGradeHorarios(gradeHorarios);
 
        return this.create(agendamento);
    }
 
    private Profissional determinarProfissionalLivre(Long profesionalId, LocalDate data, LocalTime inicio, LocalTime fim) {
        IService<Profissional> profService = ServiceRegistry.getService("Profissional");
        
        List<Profissional> candidatos = new ArrayList<>();
        if (profesionalId != null && profesionalId > 0) {
            Profissional filtro = new Profissional();
            filtro.setId(profesionalId);
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
