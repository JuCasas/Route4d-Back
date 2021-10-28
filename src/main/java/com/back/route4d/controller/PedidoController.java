package com.back.route4d.controller;

import com.back.route4d.model.Pedido;
import com.back.route4d.services.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/order")
public class PedidoController {
    private PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        super();
        this.pedidoService = pedidoService;
    }

    //Build create pedido REST API
    @PostMapping("/")
    public ResponseEntity<Pedido> savePedido(@RequestBody Pedido pedido){
        return new ResponseEntity<Pedido>(pedidoService.savePedido(pedido), HttpStatus.CREATED);
    }

    //Build get all pedidos REST API
    @GetMapping
    public List<Pedido> getAllPedidos(){
        return pedidoService.getAllPedidos();
    }

    //Build get pedido by ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable("id") int pedidoId){
        return new ResponseEntity<Pedido>(pedidoService.getPedidoById(pedidoId),HttpStatus.OK);
    }

    //Build update pedido by ID
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updateVehicle(@PathVariable("id") int pedidoId, @RequestBody Pedido pedido){
        return new ResponseEntity<Pedido>(pedidoService.updatePedido(pedido,pedidoId),HttpStatus.OK);
    }

    //Build delete pedido by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable("id") int pedidoId){
        pedidoService.deletePedido(pedidoId);
        return new ResponseEntity<String>("Pedido eliminado correctamente!",HttpStatus.OK);
    }
}
