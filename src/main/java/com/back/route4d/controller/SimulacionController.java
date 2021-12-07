package com.back.route4d.controller;

import com.back.route4d.algoritmo.Simulacion;
import com.back.route4d.model.CallesBloqueadas;
import com.back.route4d.model.Pedido;
import com.back.route4d.model.Ruta;
import com.back.route4d.model.RutaFront;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/simulacion")
public class SimulacionController {

    @Autowired
    private Simulacion simulacion;
//    private volatile boolean collect = false;
    private LinkedHashMap<String,RutaFront> rutasEnviar = new LinkedHashMap<String,RutaFront>();

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

    @GetMapping(value = "/rutas")
    public  ResponseEntity<LinkedHashMap<String,RutaFront>> rutaTraer() throws InterruptedException {
        List<RutaFront> listaRutas = simulacion.listaRutasEnRecorrido;

        for (int i=0;i<listaRutas.size();i++){
            rutasEnviar.put(listaRutas.get(i).getVehiculo().getPlaca(),listaRutas.get(i));
        }

        Thread.sleep(300);
        simulacion.collect = true;
        return new ResponseEntity<>(rutasEnviar,HttpStatus.OK);
    }

    @GetMapping(value = "/no")
    public ResponseEntity<List<Pedido>> pedidosSin(){
        return new ResponseEntity<>(simulacion.listaPedidosSinCumplir,HttpStatus.OK);
    }
}
