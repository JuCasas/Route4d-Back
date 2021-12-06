package com.back.route4d.controller;

import com.back.route4d.algoritmo.Simulacion;
import com.back.route4d.model.CallesBloqueadas;
import com.back.route4d.model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/simulacion")
public class SimulacionController {

    @Autowired
    private Simulacion simulacion;

    @PostMapping(value = "/uploadPedidos")
    public ResponseEntity<String> uploadPedidos(@RequestParam(value = "file") MultipartFile file){
        return new ResponseEntity<>(simulacion.uploadOrdersFile(file), HttpStatus.OK);
    }

    @GetMapping(value = "/getPedidos")
    public ResponseEntity<List<Pedido>> getPedidos(){
        return new ResponseEntity<>(simulacion.getOrders(), HttpStatus.OK);
    }

    @PostMapping(value = "/uploadCallesBloqueadas")
    public ResponseEntity<String> uploadCallesBloqueadas(@RequestParam(value = "file") MultipartFile file){
        return new ResponseEntity<>(simulacion.uploadClosedRoadsFile(file), HttpStatus.OK);
    }

    @GetMapping(value = "/getListaCallesBloqueadas")
    public ResponseEntity<List<CallesBloqueadas>> getListaCallesBloqueadas(){
        return new ResponseEntity<>(simulacion.getClosedRoads(), HttpStatus.OK);
    }

    @PostMapping(value = "/empezar")
    public ResponseEntity<String> empezarSimulacion(){
        simulacion.inicializar();
        return new ResponseEntity<>("Simulando", HttpStatus.OK);
    }

    @PostMapping(value = "/reiniciar")
    public ResponseEntity<String> reiniciarSimulacion(){
        simulacion.reiniciarSimulacion();
        return new ResponseEntity<>("Reiniciado", HttpStatus.OK);
    }

}
