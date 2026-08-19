package com.gwj.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gwj.model.domain.entities.Setting;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

@Controller
@RequestMapping("/MRYnZpAsC9sp/configuracoes")
public class AdminSettingController {

    @GetMapping({"", "/"})
    public String listar(Model model) {
        try {
            IService<Setting> service = ServiceRegistry.getService("Setting");
            List<Setting> settings = service.read(new Setting());
            model.addAttribute("settings", settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/setting/store-setting/form";
    }
}
