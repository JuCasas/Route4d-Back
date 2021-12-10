package com.back.route4d.controller;

import com.back.route4d.services.AlgoritmoService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/algoritmo")
public class AlgoritmoController {
    private AlgoritmoService algoritmoService;

    public AlgoritmoController(AlgoritmoService algoritmoService) {
        super();
        this.algoritmoService = algoritmoService;
    }

    @GetMapping
    public void getRoutes() {
        algoritmoService.generarRutasBBDD();
    }

    @PostMapping("/operacion")
    public HashMap operacion(@RequestBody Map<String, String> json) {
        String k = json.get("k");
        String Sa = json.get("sa");

        return algoritmoService.enviarRutasOperacion(k, Sa);
    }

}
