package com.back.route4d.controller;
import com.back.route4d.model.Averia;
import com.back.route4d.model.Vehicle;
import com.back.route4d.services.AveriaService;
import com.back.route4d.services.PedidoService;
import com.back.route4d.services.RutaService;
import com.back.route4d.services.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/vehicle")
public class VehicleController {

    private VehicleService vehicleService;
    private PedidoService pedidoService;
    private AveriaService averiaService;
    private RutaService rutaService;

    public VehicleController(VehicleService vehicleService, PedidoService pedidoService,
                             AveriaService averiaService, RutaService rutaService) {
        super();
        this.vehicleService = vehicleService;
        this.pedidoService = pedidoService;
        this.averiaService = averiaService;
        this.rutaService = rutaService;
    }

    //Build create vehicle REST API


    @PostMapping("/")
    public ResponseEntity<Vehicle> saveVehicle(@RequestBody Map<String, String> nuevo){
        return new ResponseEntity<Vehicle>(vehicleService.saveVehicle(nuevo), HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PatchMapping("/{id}")
    public ResponseEntity<Vehicle> patch(@PathVariable("id") int idVehicle, @RequestBody Map<Object, Object> campos) {
        return new ResponseEntity<Vehicle>(vehicleService.patch(idVehicle,campos),HttpStatus.OK);
    }


    @GetMapping("/tipo/{id}")
    public List<Vehicle> getVehiclesType(@PathVariable("id") int tipoId){
        return vehicleService.getAllType(tipoId);
    }

    //Build get all vehicles REST API
    @GetMapping
    public List<Vehicle> getAllVehicles(){
        return vehicleService.getAllVehicles();
    }

    //Build get vehicle by ID
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable("id") int vehicleId){
        return new ResponseEntity<Vehicle>(vehicleService.getVehicleById(vehicleId),HttpStatus.OK);
    }

    //Build update vehicle by ID
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable("id") int vehicleId, @RequestBody Vehicle vehicle){
        return new ResponseEntity<Vehicle>(vehicleService.updateVehicle(vehicle,vehicleId),HttpStatus.OK);
    }

    //Build delete vehicle by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable("id") int vehicleId){
        vehicleService.deleteVehicle(vehicleId);
        return new ResponseEntity<String>("Vehiculo eliminado correctamente!",HttpStatus.OK);
    }

    // Mostrar averías
    @GetMapping("/mostrarAverias")
    public List<Averia> getAllAverias() {
        return averiaService.getAllAverias();
    }

    @PostMapping("/averia")
    public ResponseEntity<Averia> registrarAveria(@RequestBody Map<String, Object> json) {
        LocalDateTime now = LocalDateTime.now();
        int idVehiculo = (int)json.get("idVehiculo");
        List<Integer> pedidos = (List<Integer>)json.get("pedidos");

        // averiando vehículo y desasignando pedidos
        vehicleService.averiarVehicle(idVehiculo);
        rutaService.setRutaAsAveriada(now, idVehiculo);
        for (Integer idPedido : pedidos) {
            pedidoService.desasignarPedido(idPedido);
            rutaService.unlinkPedidoFromRuta(idPedido);
        }

        // creando avería
        Averia averia = new Averia();
        Vehicle vehicle = vehicleService.getVehicleById(idVehiculo);
        averia.setVehicle(vehicle);
        averia.setFechaAveria(now);
        averiaService.saveAveria(averia);

        return new ResponseEntity<Averia>(averia, HttpStatus.CREATED);
    }
}