package com.gwj.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HoneypotController {

    /**
     * Armadilha (Honeypot) para bots e invasores.
     * Responde requisições para /admin (e subpastas) com status 403 Forbidden instantâneo.
     */
    @RequestMapping(value = {"/admin", "/admin/**"})
    public ResponseEntity<String> blockAdminHoneypot() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("403 Forbidden - Access Denied");
    }
}