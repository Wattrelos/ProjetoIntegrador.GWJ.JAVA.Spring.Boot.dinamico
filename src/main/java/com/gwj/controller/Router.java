package com.gwj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.gwj.model.dataTransferObject.EntityMapper;
import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.factory.SimpleObjectFactory;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.gwj.model.domain.entities.Usuario;
import com.gwj.model.domain.entities.Cliente;
import com.gwj.model.domain.entities.Agendamento;
import java.util.List;

@Controller
public class Router {

    @GetMapping({"/home","/"})
    public String home(Model model) {
        IEntity servicoBase = SimpleObjectFactory.create("Servico");
        IService<IEntity> service = ServiceRegistry.getService("Servico");
        List<IEntity> servicos = service.read(servicoBase);
        model.addAttribute("servicos", servicos);
        return "home";
    }

    @GetMapping("/servicos")
    public String servico(Model model) {
        IEntity servicoBase = SimpleObjectFactory.create("Servico");
        IService<IEntity> servicoService = ServiceRegistry.getService("Servico");
        List<IEntity> servicos = servicoService.read(servicoBase);
        model.addAttribute("servicos", servicos);

        IEntity profissionalBase = SimpleObjectFactory.create("Profissional");
        IService<IEntity> profService = ServiceRegistry.getService("Profissional");
        List<IEntity> profissionais = profService.read(profissionalBase);
        model.addAttribute("profissionais", profissionais);

        return "servicos";
    }

    @GetMapping("/sobre-nos")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/contato")
    public String contato() {
        return "contato";
    }

    @GetMapping({"/cart","/carrinho"})
    public String carrinho(){
        return "carrinho";
    }    

    @GetMapping("/checkout")
    public String checkout(
            @RequestParam("servicoId") Long servicoId,
            @RequestParam(value = "profissionalId", required = false) Long profissionalId,
            @RequestParam("dataHora") String dataHoraStr,
            HttpServletRequest request,
            Model model) {
        
        try {
            // Carregar Serviço
            IService<com.gwj.model.domain.entities.Servico> servicoService = ServiceRegistry.getService("Servico");
            com.gwj.model.domain.entities.Servico sFiltro = new com.gwj.model.domain.entities.Servico();
            sFiltro.setId(servicoId);
            List<com.gwj.model.domain.entities.Servico> servicos = servicoService.read(sFiltro);
            if (!servicos.isEmpty()) {
                model.addAttribute("servico", servicos.get(0));
            }

            // Carregar Profissional
            if (profissionalId != null && profissionalId > 0) {
                IService<com.gwj.model.domain.entities.Profissional> profService = ServiceRegistry.getService("Profissional");
                com.gwj.model.domain.entities.Profissional pFiltro = new com.gwj.model.domain.entities.Profissional();
                pFiltro.setId(profissionalId);
                List<com.gwj.model.domain.entities.Profissional> profissionais = profService.read(pFiltro);
                if (!profissionais.isEmpty()) {
                    model.addAttribute("profissional", profissionais.get(0));
                }
            } else {
                model.addAttribute("profissionalNome", "Qualquer profissional livre");
            }

            // Passar os dados brutos de agendamento
            model.addAttribute("servicoId", servicoId);
            model.addAttribute("profissionalId", profissionalId != null ? profissionalId : 0L);
            model.addAttribute("dataHora", dataHoraStr);

            // Formatar a dataHora de forma amigável para exibição
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(dataHoraStr);
            java.time.format.DateTimeFormatter formatador = java.time.format.DateTimeFormatter.ofPattern("dd 'de' MMMM', às' HH:mm", new java.util.Locale("pt", "BR"));
            model.addAttribute("dataHoraFormatada", dt.format(formatador));

            // Verificar se o usuário logado está na sessão e carregar dados do Cliente associado
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            if (session != null) {
                com.gwj.model.domain.entities.Usuario usuarioLogado = (com.gwj.model.domain.entities.Usuario) session.getAttribute("usuarioLogado");
                if (usuarioLogado != null) {
                    IService<com.gwj.model.domain.entities.Cliente> clienteService = ServiceRegistry.getService("Cliente");
                    com.gwj.model.domain.entities.Cliente cFiltro = new com.gwj.model.domain.entities.Cliente();
                    cFiltro.setId(usuarioLogado.getId());
                    List<com.gwj.model.domain.entities.Cliente> clientes = clienteService.read(cFiltro);
                    if (!clientes.isEmpty()) {
                        model.addAttribute("clienteLogado", clientes.get(0));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "checkout";
    }

    @GetMapping("/order-confirmation")
    public String orderConfirmation(@RequestParam("id") Long agendamentoId, Model model) {
        try {
            IService<com.gwj.model.domain.entities.Agendamento> agendamentoService = ServiceRegistry.getService("Agendamento");
            com.gwj.model.domain.entities.Agendamento aFiltro = new com.gwj.model.domain.entities.Agendamento();
            aFiltro.setId(agendamentoId);
            List<com.gwj.model.domain.entities.Agendamento> resultados = agendamentoService.read(aFiltro);
            if (!resultados.isEmpty()) {
                com.gwj.model.domain.entities.Agendamento agendamento = resultados.get(0);
                model.addAttribute("agendamento", agendamento);
 
                // Formata a data de forma amigável
                if (agendamento.getDataAgendamento() != null && agendamento.getHoraInicio() != null) {
                    java.time.LocalDateTime dt = java.time.LocalDateTime.of(agendamento.getDataAgendamento(), agendamento.getHoraInicio());
                    java.time.format.DateTimeFormatter formatador = java.time.format.DateTimeFormatter.ofPattern("dd 'de' MMMM', às' HH:mm", new java.util.Locale("pt", "BR"));
                    model.addAttribute("dataHoraFormatada", dt.format(formatador));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "order-confirmation";
    }
    
    @GetMapping("loja")
    public String shop(Model model) {
        IEntity produtoBase = SimpleObjectFactory.create("Produto");
        IService<IEntity> service = ServiceRegistry.getService("Produto");
        List<IEntity> produtos = service.read(produtoBase);
        model.addAttribute("produtos", produtos);
        return "loja";
    }

    @GetMapping("/single-product")
    public String singleProduct(HttpServletRequest request, Model model) {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            IEntity produtoBase = SimpleObjectFactory.create("Produto");
            IEntity filtro = EntityMapper.fillEntity(produtoBase, request);
            IService<IEntity> service = ServiceRegistry.getService("Produto");
            List<IEntity> resultados = service.read(filtro);
            if (!resultados.isEmpty()) {
                model.addAttribute("produto", resultados.get(0));
            }
        }
        return "single-product";
    }

    @GetMapping({"/pedidos", "/meus-agendamentos"})
    public String meusAgendamentos(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        
        try {
            // Buscar dados do Cliente completo
            IService<Cliente> clienteService = ServiceRegistry.getService("Cliente");
            Cliente cFiltro = new Cliente();
            cFiltro.setId(usuarioLogado.getId());
            List<Cliente> clientes = clienteService.read(cFiltro);
            
            if (!clientes.isEmpty()) {
                Cliente cliente = clientes.get(0);
                model.addAttribute("clienteLogado", cliente);
                
                // Buscar agendamentos do telefone correspondente
                IService<Agendamento> agendamentoService = ServiceRegistry.getService("Agendamento");
                Agendamento aFiltro = new Agendamento();
                aFiltro.setClienteTelefone(cliente.getTelefone());
                List<Agendamento> agendamentos = agendamentoService.read(aFiltro);
                
                // Ordenar do mais recente para o mais antigo
                agendamentos.sort((a1, a2) -> {
                    if (a1.getDataAgendamento() == null || a2.getDataAgendamento() == null) return 0;
                    int compData = a2.getDataAgendamento().compareTo(a1.getDataAgendamento());
                    if (compData != 0) return compData;
                    if (a1.getHoraInicio() == null || a2.getHoraInicio() == null) return 0;
                    return a2.getHoraInicio().compareTo(a1.getHoraInicio());
                });
                
                model.addAttribute("agendamentos", agendamentos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "meus-agendamentos";
    }
}
