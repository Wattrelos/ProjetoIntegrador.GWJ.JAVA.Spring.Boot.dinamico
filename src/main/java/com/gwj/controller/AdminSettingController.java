package com.gwj.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gwj.model.domain.entities.Setting;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;
import com.gwj.service.SettingService;

@Controller
@RequestMapping("/MRYnZpAsC9sp/configuracoes")
public class AdminSettingController {

    @GetMapping({"", "/"})
    public String form(Model model) {
        try {
            IService<Setting> genericService = ServiceRegistry.getService("Setting");
            SettingService service = (SettingService) genericService;
            Map<String, String> settings = service.getAllAsMap();
            model.addAttribute("settings", settings);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar configurações: " + e.getMessage());
            e.printStackTrace();
        }
        return "admin/setting/store-setting/form";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        try {
            IService<Setting> genericService = ServiceRegistry.getService("Setting");
            SettingService service = (SettingService) genericService;
            service.updateSettings(params);
            redirectAttributes.addFlashAttribute("sucesso", "Configurações da barbearia atualizadas com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar configurações: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/MRYnZpAsC9sp/configuracoes";
    }
}
