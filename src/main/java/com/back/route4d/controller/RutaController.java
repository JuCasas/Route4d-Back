package com.back.route4d.controller;

import com.back.route4d.services.RutaService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/rutas")
public class RutaController {
    private RutaService rutaService;

    public RutaController(RutaService rutaService) {
        super();
        this.rutaService = rutaService;
    }

    @GetMapping
    public HashMap getRutas() {
        return rutaService.enviarRutas();
    }
}
