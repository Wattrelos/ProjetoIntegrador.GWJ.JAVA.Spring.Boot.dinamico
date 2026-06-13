package com.gwj.controller;
 
import com.gwj.model.domain.entities.Agendamento;
import com.gwj.service.AgendamentoService;
import com.gwj.service.ServiceRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
 
@Controller
public class AgendaController {
 
    @GetMapping("/api/agenda/horarios-disponiveis")
    @ResponseBody
    public ResponseEntity<?> getHorariosDisponiveis(
            @RequestParam("servicoId") Long servicoId,
            @RequestParam(value = "profissionalId", required = false) Long profissionalId,
            @RequestParam("data") String data) {
        try {
            com.gwj.service.IService<Agendamento> genericService = ServiceRegistry.getService("Agendamento");
            AgendamentoService agendamentoService = (AgendamentoService) genericService;
            List<Map<String, Object>> slots = agendamentoService.getHorariosDisponiveis(servicoId, profissionalId, data);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao buscar horários: " + e.getMessage());
        }
    }
 
    @PostMapping("/checkout/confirmar")
    public String confirmarReserva(
            @RequestParam("servicoId") Long servicoId,
            @RequestParam(value = "profissionalId", required = false) Long profissionalId,
            @RequestParam("dataHora") String dataHora,
            @RequestParam("nome") String nome,
            @RequestParam("sobrenome") String sobrenome,
            @RequestParam("email") String email,
            @RequestParam("telefone") String telefone,
            @RequestParam(value = "pagamento", required = false) String pagamento,
            RedirectAttributes redirectAttributes) {
        try {
            com.gwj.service.IService<Agendamento> genericService = ServiceRegistry.getService("Agendamento");
            AgendamentoService agendamentoService = (AgendamentoService) genericService;
 
            // Converte o dataHora consolidado do formulário (ex: 2026-06-15T09:00:00) em partes de LocalDate/LocalTime
            LocalDateTime ldt = LocalDateTime.parse(dataHora);
            String dataStr = ldt.toLocalDate().toString();
            String horaStr = ldt.toLocalTime().toString();
 
            Agendamento agendamentoSalva = agendamentoService.confirmarReserva(
                    servicoId, profissionalId, dataStr, horaStr, nome, sobrenome, email, telefone
            );
            return "redirect:/order-confirmation?id=" + agendamentoSalva.getId();
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            // Redireciona de volta para a página de checkout preservando os parâmetros da url
            String redirectUrl = "redirect:/checkout?servicoId=" + servicoId + "&dataHora=" + dataHora;
            if (profissionalId != null && profissionalId > 0) {
                redirectUrl += "&profissionalId=" + profissionalId;
            }
            return redirectUrl;
        }
    }
}
