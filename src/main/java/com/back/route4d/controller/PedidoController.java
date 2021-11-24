package com.back.route4d.controller;

import com.back.route4d.model.Pedido;
import com.back.route4d.services.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/filterDate")
    public HashMap filterByDate(@RequestBody Map<String, String> json){
        //String inicio = "2016-03-04 11:30";
        String inicio = json.get("inicio");
        String fin = json.get("fin");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m");
        LocalDateTime dateTimeInicio = LocalDateTime.parse(inicio, formatter);
        LocalDateTime dateTimeFin = LocalDateTime.parse(fin, formatter);


        List<Pedido> lista = pedidoService.findByDate(dateTimeInicio,dateTimeFin);

        HashMap <String, Integer> meses = new LinkedHashMap<>();

        meses.put("Enero",0);
        meses.put("Febrero",0);
        meses.put("Marzo",0);
        meses.put("Abril",0);
        meses.put("Mayo",0);
        meses.put("Junio",0);
        meses.put("Julio",0);
        meses.put("Agosto",0);
        meses.put("Septiembre",0);
        meses.put("Octubre",0);
        meses.put("Noviembre",0);
        meses.put("Diciembre",0);

        for (Pedido pedido:lista){
            int mes = pedido.getFechaPedido().getMonthValue();
            switch (mes){
                case 1:
                    meses.put("Enero", meses.get("Enero") + 1);
                    break;
                case 2:
                    meses.put("Febrero", meses.get("Febrero") + 1);
                    break;
                case 3:
                    meses.put("Marzo", meses.get("Marzo") + 1);
                    break;
                case 4:
                    meses.put("Abril", meses.get("Abril") + 1);
                    break;
                case 5:
                    meses.put("Mayo", meses.get("Mayo") + 1);
                    break;
                case 6:
                    meses.put("Junio", meses.get("Junio") + 1);
                    break;
                case 7:
                    meses.put("Julio", meses.get("Julio") + 1);
                    break;
                case 8:
                    meses.put("Agosto", meses.get("Agosto") + 1);
                    break;
                case 9:
                    meses.put("Septiembre", meses.get("Septiembre") + 1);
                    break;
                case 10:
                    meses.put("Octubre", meses.get("Octubre") + 1);
                    break;
                case 11:
                    meses.put("Noviembre", meses.get("Noviembre") + 1);
                    break;
                case 12:
                    meses.put("Diciembre", meses.get("Diciembre") + 1);
                    break;

            }
        }

        return meses;
    }
}
