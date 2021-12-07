package com.back.route4d.controller;
import com.back.route4d.message.ResponseMessage;
import com.back.route4d.model.Vehicle;
import com.back.route4d.services.PedidoService;
import com.back.route4d.services.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/vehicle")
public class VehicleController {

    private VehicleService vehicleService;
    private PedidoService pedidoService;

    public VehicleController(VehicleService vehicleService, PedidoService pedidoService) {
        super();
        this.vehicleService = vehicleService;
        this.pedidoService = pedidoService;
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

    @PostMapping("/averia")
    public ResponseEntity<ResponseMessage> registrarAveria(@RequestBody Map<String, Object> json) {
        String message = "";
        int idVehiculo = (int)json.get("idVehiculo");
        List<Integer> pedidos = (List<Integer>)json.get("pedidos");

        vehicleService.averiarVehicle(idVehiculo);
        for (Integer idPedido : pedidos) {
            pedidoService.desasignarPedido(idPedido);
        }

        message = "done!";
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    }
}