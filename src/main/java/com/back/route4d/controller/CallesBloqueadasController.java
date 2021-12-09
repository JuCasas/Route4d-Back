package com.back.route4d.controller;

import com.back.route4d.services.CallesBloqueadasService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/bloqueos")
public class CallesBloqueadasController {
    private CallesBloqueadasService callesBloqueadasService;

    public CallesBloqueadasController(CallesBloqueadasService callesBloqueadasService) {
        super();
        this.callesBloqueadasService = callesBloqueadasService;
    }

    @GetMapping("/dia")
    public HashMap getBloqueos() {
        return callesBloqueadasService.enviarBloqueos();
    }
}
