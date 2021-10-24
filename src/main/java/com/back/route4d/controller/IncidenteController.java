package com.back.route4d.controller;

import com.back.route4d.model.Incidente;
import com.back.route4d.services.IncidenteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidente")
public class IncidenteController  {
    private IncidenteService incidenteService;

    public IncidenteController(IncidenteService incidenteService) {
        super();
        this.incidenteService = incidenteService;
    }

    //Build create vehicle REST API
    @PostMapping("/")
    public ResponseEntity<Incidente> saveIncidente(@RequestBody Incidente incidente){
        return new ResponseEntity<Incidente>(incidenteService.saveIncidente(incidente), HttpStatus.CREATED);
    }


    //Build get vehicle by ID
    @GetMapping("/{id}")
    public ResponseEntity<Incidente> getIncidenteById(@PathVariable("id") int vehicleId){
        return new ResponseEntity<Incidente>(incidenteService.getIncidenteById(vehicleId),HttpStatus.OK);
    }


}

