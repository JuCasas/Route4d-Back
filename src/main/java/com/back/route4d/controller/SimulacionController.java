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
        return new ResponseEntity<>(simulacion.subirArchivoPedidos(file), HttpStatus.OK);
    }

    @GetMapping(value = "/getPedidos")
    public ResponseEntity<List<Pedido>> getPedidos(){
        return new ResponseEntity<>(simulacion.getPedidos(), HttpStatus.OK);
    }

    @PostMapping(value = "/uploadCallesBloqueadas")
    public ResponseEntity<String> uploadCallesBloqueadas(@RequestParam(value = "file") MultipartFile file){
        return new ResponseEntity<>(simulacion.subirArchivoCallesBloqueadas(file), HttpStatus.OK);
    }

    @GetMapping(value = "/getListaCallesBloqueadas")
    public ResponseEntity<List<CallesBloqueadas>> getListaCallesBloqueadas(){
        return new ResponseEntity<>(simulacion.getListaCallesBloqueadas(), HttpStatus.OK);
    }


}
