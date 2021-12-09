package com.back.route4d.controller;

import com.back.route4d.helper.Helper;
import com.back.route4d.message.ResponseMessage;
import com.back.route4d.model.Pedido;
import com.back.route4d.services.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setFechaLimite(pedido.getFechaPedido().plus(pedido.getMinFaltantes(), ChronoUnit.HOURS));
        pedido.setMinFaltantes(Helper.convertLocalDateTimeToMinutes(pedido.getFechaLimite()));

        if(pedido.getCantidad()>25){
            int cociente = pedido.getCantidad()/5;
            int residuo = pedido.getCantidad()%5;

            for (int i=0;i<cociente;i++){
                Pedido pedido1 = new Pedido(pedido.getNodoId(),pedido.getX(),pedido.getY(),
                        5,pedido.getMinFaltantes(),pedido.getFechaPedido(),
                        pedido.getFechaLimite(),pedido.getTipoPedido());

                pedidoService.savePedido(pedido1);
            }
            if(residuo>0){
                Pedido pedido2 = new Pedido(pedido.getNodoId(),pedido.getX(),pedido.getY(),
                        residuo,pedido.getMinFaltantes(),pedido.getFechaPedido(),
                        pedido.getFechaLimite(),pedido.getTipoPedido());
                pedidoService.savePedido(pedido2);
            }
            return new ResponseEntity<Pedido>(pedido, HttpStatus.CREATED);
        }

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
    public List<Map<String, Object>> filterByDate(@RequestBody Map<String, String> json){
        //String inicio = "2016-03-04 11:30";
        String inicio = json.get("inicio");
        String fin = json.get("fin");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m");
        LocalDateTime dateTimeInicio = LocalDateTime.parse(inicio, formatter);
        LocalDateTime dateTimeFin = LocalDateTime.parse(fin, formatter);


        List<Pedido> lista = pedidoService.findByDate(dateTimeInicio,dateTimeFin);

        List<Map<String,Object>> data = new ArrayList<>();
        List<String> mesesNombre = Arrays.asList("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");

        for (String nombre:mesesNombre){
            Map<String ,Object> mes=new HashMap<String,Object>();
            mes.put("Mes",nombre);
            mes.put("Data",0);
            data.add(mes);
        }


        for (Pedido pedidoEvaluar:lista){
            int num = pedidoEvaluar.getFechaPedido().getMonthValue();
            Map<String ,Object> mes = data.get(num-1);
            mes.put("Data", (int) mes.get("Data") + 1);
        }

        return data;
    }

}
