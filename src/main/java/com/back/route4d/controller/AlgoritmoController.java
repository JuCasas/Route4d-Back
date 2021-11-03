package com.back.route4d.controller;

import com.back.route4d.model.Incidente;
import com.back.route4d.services.AlgoritmoService;
import com.back.route4d.services.IncidenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/algoritmo")
public class AlgoritmoController {
    private AlgoritmoService algoritmoService;

    public AlgoritmoController(AlgoritmoService algoritmoService) {
        super();
        this.algoritmoService = algoritmoService;
    }


    //Build get vehicle by ID
    @GetMapping("/")
    public ArrayList getRoutes(){
        return algoritmoService.enviarRutas();
    }


}
