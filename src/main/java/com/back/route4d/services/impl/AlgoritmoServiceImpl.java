package com.back.route4d.services.impl;

import com.back.route4d.algoritmo.Algoritmo;
import com.back.route4d.repository.PedidoRepository;
import com.back.route4d.repository.RutaRepository;
import com.back.route4d.repository.VehicleRepository;
import com.back.route4d.services.AlgoritmoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AlgoritmoServiceImpl implements AlgoritmoService {

    @Autowired
    Algoritmo algoritmo;
    PedidoRepository pedidoRepository;
    VehicleRepository vehicleRepository;
    RutaRepository rutaRepository;

    public AlgoritmoServiceImpl(PedidoRepository pedidoRepository,VehicleRepository vehicleRepository, RutaRepository rutaRepository) {
        super();
        this.pedidoRepository = pedidoRepository;
        this.vehicleRepository = vehicleRepository;
        this.rutaRepository = rutaRepository;
    }



    @Override
    public HashMap enviarRutas() {
        Algoritmo algoritmo = new Algoritmo(pedidoRepository,vehicleRepository,rutaRepository);
        algoritmo.obtenerListaPedidos();
        algoritmo.inicializar();
        HashMap list = algoritmo.resolver();
        return list;
    }

    @Override
    public HashMap enviarRutasOperacion(String k, String sa) {
//        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        LocalDateTime initialDate = LocalDateTime.of(2022, Month.DECEMBER, 02, 0, 0, 0);
//        LocalDateTime finalDate = initialDate.plus(Duration.of(Integer.parseInt(k)*Integer.parseInt(sa), ChronoUnit.MINUTES));
//        LocalDateTime finalDate = LocalDateTime.of(2022, Month.DECEMBER, 3, 0, 33, 0);
        Algoritmo algoritmo = new Algoritmo(pedidoRepository,vehicleRepository,rutaRepository);
        LocalDateTime finalDate = LocalDateTime.now();
        //TODO cambiar estado
        algoritmo.listaPedidos = pedidoRepository.findLessThanDate(finalDate,1);
        String message = algoritmo.inicializar();
        if (message=="correcto"){
            HashMap list = algoritmo.resolver();
            System.out.println("Rutas generadas...");
        }else {
            System.out.println(message);
        }
        HashMap<String,String> map = new HashMap<>();
        return map;
    }
}
