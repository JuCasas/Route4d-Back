package com.back.route4d.controller;

import com.back.route4d.algoritmo.Simulacion;
import com.back.route4d.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/simulacion")
public class SimulacionController {

    @Autowired
    private Simulacion simulacion;
    private LinkedHashMap<String,RutaFront> rutaPlaca = new LinkedHashMap<String,RutaFront>();
    private LinkedHashMap<String,List<RutaFront>> rutasIndividuales = new LinkedHashMap<String,List<RutaFront>>();
    private LinkedHashMap<String,List<RutaFront>> reporte = new LinkedHashMap<String,List<RutaFront>>();
    private int cantVehiculos1 = 2;
    private int cantVehiculos2 = 4;
    private int cantVehiculos3 = 4;
    private int cantVehiculos4 = 10;


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


    @GetMapping(value = "/ruta/{placa}")
    public ResponseEntity<RutaFront> rutaIndividual(@PathVariable String placa){
        if(simulacion.rutasEnviar.get(placa).size()>0){
            return new ResponseEntity<>(simulacion.rutasEnviar.get(placa).remove(0),HttpStatus.OK);
        }else {
            RutaFront ruta = new RutaFront();
            ruta.setId(-1);
            ruta.getVehiculo().setPlaca(placa);
            return new ResponseEntity<>(ruta,HttpStatus.OK);
        }
    }


    @GetMapping(value = "/empezar")
    public ResponseEntity<Integer> empezarSimulacion(){
        RutaFront rutaVacia = new RutaFront();
        for (int i = 0; i < cantVehiculos1; i++) {
            rutaPlaca.put("A"+Integer.toString(i),rutaVacia);
        }
        for (int i = 0; i < cantVehiculos2; i++) {
            rutaPlaca.put("B"+Integer.toString(i),rutaVacia);
        }
        for (int i = 0; i < cantVehiculos3; i++) {
            rutaPlaca.put("C"+Integer.toString(i),rutaVacia);
        }
        for (int i = 0; i < cantVehiculos4; i++) {
            rutaPlaca.put("D"+Integer.toString(i),rutaVacia);
        }

        //PEIDDO MOMO
        for (int i = 0; i < cantVehiculos1; i++) {
            List<RutaFront> listaRuta = new ArrayList<>();
            rutasIndividuales.put("A"+Integer.toString(i),listaRuta);
        }
        for (int i = 0; i < cantVehiculos2; i++) {
            List<RutaFront> listaRuta = new ArrayList<>();
            rutasIndividuales.put("B"+Integer.toString(i),listaRuta);
        }
        for (int i = 0; i < cantVehiculos3; i++) {
            List<RutaFront> listaRuta = new ArrayList<>();
            rutasIndividuales.put("C"+Integer.toString(i),listaRuta);
        }
        for (int i = 0; i < cantVehiculos4; i++) {
            List<RutaFront> listaRuta = new ArrayList<>();
            rutasIndividuales.put("D"+Integer.toString(i),listaRuta);
        }

        for (int i = 0; i < cantVehiculos1; i++) {
            List<RutaFront> listaRuta = new ArrayList<>();
            reporte.put("A"+Integer.toString(i),listaRuta);
        }
        for (int i = 0; i < cantVehiculos2; i++) {
            List<RutaFront> listaRuta = new ArrayList<>();
            reporte.put("B"+Integer.toString(i),listaRuta);
        }
        for (int i = 0; i < cantVehiculos3; i++) {
            List<RutaFront> listaRuta = new ArrayList<>();
            reporte.put("C"+Integer.toString(i),listaRuta);
        }
        for (int i = 0; i < cantVehiculos4; i++) {
            List<RutaFront> listaRuta = new ArrayList<>();
            reporte.put("D"+Integer.toString(i),listaRuta);
        }

        simulacion.rutasReporte = reporte;

        simulacion.rutasEnviar = rutasIndividuales;

        simulacion.inicializar();

        if(simulacion.listaPedidosSinCumplir.size() != 0 ) return new ResponseEntity<>(0, HttpStatus.OK);
        else return new ResponseEntity<>(1,HttpStatus.OK);
    }

    @GetMapping(value = "/reiniciar")
    public ResponseEntity<String> reiniciarSimulacion(){
        simulacion.reiniciarSimulacion();
        return new ResponseEntity<>("Reiniciado", HttpStatus.OK);
    }

    @GetMapping(value = "/reporte")
    public ResponseEntity<LinkedHashMap<String,List<RutaFront>>> reporte(){
        return new ResponseEntity<>(simulacion.rutasReporte, HttpStatus.OK);
    }


    @GetMapping(value = "/rutasSin")
    public  ResponseEntity<List<RutaFront>> rutaSinActualizar() {
        return new ResponseEntity<>(simulacion.listaRutasEnRecorrido,HttpStatus.OK);
    }

        @GetMapping(value = "/rutasInit")
    public  ResponseEntity<LinkedHashMap<String,RutaFront>> rutasIniciales() throws InterruptedException {
            LinkedHashMap<String,RutaFront> rutasIniciales = new LinkedHashMap<String,RutaFront>();

        for (int i = 0; i < cantVehiculos1; i++) {
            RutaFront rutaVacia = new RutaFront();
            String placa = "A"+Integer.toString(i);
            rutaVacia.getVehiculo().setPlaca(placa);
            if(simulacion.rutasEnviar.get(placa).size()>0){
                rutasIniciales.put("A"+Integer.toString(i),simulacion.rutasEnviar.get(placa).remove(0));
            }else {
                rutasIniciales.put("A"+Integer.toString(i),rutaVacia);
            }
        }

        for (int i = 0; i < cantVehiculos2; i++) {
            RutaFront rutaVacia = new RutaFront();
            String placa = "B"+Integer.toString(i);
            rutaVacia.getVehiculo().setPlaca(placa);
            if(simulacion.rutasEnviar.get(placa).size()>0){
                rutasIniciales.put("B"+Integer.toString(i),simulacion.rutasEnviar.get(placa).remove(0));
            }else {
                rutasIniciales.put("B"+Integer.toString(i),rutaVacia);
            }
        }

        for (int i = 0; i < cantVehiculos3; i++) {
            RutaFront rutaVacia = new RutaFront();
            String placa = "C"+Integer.toString(i);
            rutaVacia.getVehiculo().setPlaca(placa);
            if(simulacion.rutasEnviar.get(placa).size()>0){
                rutasIniciales.put("C"+Integer.toString(i),simulacion.rutasEnviar.get(placa).remove(0));
            }else {
                rutasIniciales.put("C"+Integer.toString(i),rutaVacia);
            }
        }

        for (int i = 0; i < cantVehiculos4; i++) {
            RutaFront rutaVacia = new RutaFront();
            String placa = "D"+Integer.toString(i);
            rutaVacia.getVehiculo().setPlaca(placa);
            if(simulacion.rutasEnviar.get(placa).size()>0){
                rutasIniciales.put("D"+Integer.toString(i),simulacion.rutasEnviar.get(placa).remove(0));
            }else {
                rutasIniciales.put("D"+Integer.toString(i),rutaVacia);
            }
        }


        return new ResponseEntity<>(rutasIniciales,HttpStatus.OK);
    }

    @GetMapping(value = "/incumplimiento")
    public ResponseEntity<List<Pedido>> pedidosSin(){
        return new ResponseEntity<>(simulacion.listaPedidosSinCumplir,HttpStatus.OK);
    }

    @PostMapping(value = "/tipoSimulacion")
    public ResponseEntity<String> tipo(@RequestBody Map<String,Integer> json){
        return new ResponseEntity<>(simulacion.updateSimulacionTipo((int)json.get("tipo")),HttpStatus.OK);
    }
}
