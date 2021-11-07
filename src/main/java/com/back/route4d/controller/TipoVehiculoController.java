package com.back.route4d.controller;
import com.back.route4d.model.TipoVehiculo;
import com.back.route4d.services.TipoVehiculoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/tipoVehiculo")
public class TipoVehiculoController {
    private TipoVehiculoService tipoVehiculoService;

    public TipoVehiculoController(TipoVehiculoService tipoVehiculoService) {
        super();
        this.tipoVehiculoService = tipoVehiculoService;
    }

    //Build create vehicle REST API
    @PostMapping("/")
    public ResponseEntity<TipoVehiculo> saveTipoVehiculo(@RequestBody TipoVehiculo tipo){
        return new ResponseEntity<TipoVehiculo>(tipoVehiculoService.saveTipoVehiculo(tipo), HttpStatus.CREATED);
    }

    //Build get all vehicles REST API
    @GetMapping
    public List<TipoVehiculo> getAllTipoVehiculo(){
        return tipoVehiculoService.getAllTipoVehiculo();
    }

    //Build get vehicle by ID
    @GetMapping("/{id}")
    public ResponseEntity<TipoVehiculo> getTipoById(@PathVariable("id") int tipoId){
        return new ResponseEntity<TipoVehiculo>(tipoVehiculoService.getTipoById(tipoId),HttpStatus.OK);
    }

    //Build update vehicle by ID
    @PutMapping("/{id}")
    public ResponseEntity<TipoVehiculo> updateTipoVehiculo(@PathVariable("id") int tipoId, @RequestBody TipoVehiculo tipo){
        return new ResponseEntity<TipoVehiculo>(tipoVehiculoService.updateTipoVehiculo(tipo,tipoId),HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PatchMapping("/{id}")
    public ResponseEntity<TipoVehiculo> patch(@PathVariable("id") int tipoId, @RequestBody Map<Object, Object> campos) {
        return new ResponseEntity<TipoVehiculo>(tipoVehiculoService.patch(tipoId,campos),HttpStatus.OK);
    }

    //Build delete vehicle by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTipoVehiculo(@PathVariable("id") int tipoId){
        tipoVehiculoService.deleteTipoVehiculo(tipoId);
        return new ResponseEntity<String>("Tipo de Vehiculo eliminado correctamente!",HttpStatus.OK);
    }
}
