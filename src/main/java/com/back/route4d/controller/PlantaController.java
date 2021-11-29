package com.back.route4d.controller;
import com.back.route4d.model.Planta;
import com.back.route4d.services.PlantaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/planta")
public class PlantaController {

    private PlantaService plantaService;

    public PlantaController(PlantaService plantaService) {
        super();
        this.plantaService = plantaService;
    }

    //Build create vehicle REST API


    @PostMapping("/")
    public ResponseEntity<Planta> savePlanta(@RequestBody Planta planta){
        return new ResponseEntity<Planta>(plantaService.savePlanta(planta), HttpStatus.CREATED);
    }

    //Build get all vehicles REST API
    @GetMapping
    public List<Planta> getAllPlantas(){
        return plantaService.getAllPlantas();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Planta> updatePlanta(@PathVariable("id") int id, @RequestBody Planta planta){
        return new ResponseEntity<Planta>(plantaService.updatePlanta(planta,id),HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PatchMapping("/{id}")
    public ResponseEntity<Planta> patch(@PathVariable("id") int idPlanta, @RequestBody Map<Object, Object> campos) {
        return new ResponseEntity<Planta>(plantaService.patch(idPlanta,campos),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlanta(@PathVariable("id") int id){
        plantaService.deletePlanta(id);
        return new ResponseEntity<String>("Planta eliminada correctamente!",HttpStatus.OK);
    }
}
