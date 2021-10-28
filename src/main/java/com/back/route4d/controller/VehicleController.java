package com.back.route4d.controller;

import com.back.route4d.model.Vehicle;
import com.back.route4d.services.VehicleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/vehicle")
public class VehicleController {

    private VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        super();
        this.vehicleService = vehicleService;
    }

    //Build create vehicle REST API
    @PostMapping("/")
    public ResponseEntity<Vehicle> saveVehicle(@RequestBody Vehicle vehicle){
        return new ResponseEntity<Vehicle>(vehicleService.saveVehicle(vehicle), HttpStatus.CREATED);
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
}
