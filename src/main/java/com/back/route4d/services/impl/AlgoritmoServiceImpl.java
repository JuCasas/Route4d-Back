package com.back.route4d.services.impl;

import com.back.route4d.algoritmo.Algoritmo;
import com.back.route4d.repository.PedidoRepository;
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

    public AlgoritmoServiceImpl(PedidoRepository pedidoRepository,VehicleRepository vehicleRepository) {
        super();
        this.pedidoRepository = pedidoRepository;
        this.vehicleRepository = vehicleRepository;
    }



    @Override
    public HashMap enviarRutas() {
        Algoritmo algoritmo = new Algoritmo(pedidoRepository,vehicleRepository);
        algoritmo.obtenerListaPedidos();
        algoritmo.inicializar();
        HashMap list = algoritmo.resolver();
        return list;
    }

    @Override
    public String hola() {
        Algoritmo algoritmo = new Algoritmo(pedidoRepository,vehicleRepository);
        LocalDateTime finalDate = LocalDateTime.of(2022, Month.DECEMBER, 3, 0, 33, 0);
        algoritmo.listaPedidos = pedidoRepository.findLessThanDate(finalDate,0);
        algoritmo.inicializar();
        return "Hola Pe";
    }

    @Override
    public HashMap enviarRutasOperacion(String k, String sa) {
//        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        LocalDateTime initialDate = LocalDateTime.of(2022, Month.DECEMBER, 02, 0, 0, 0);
//        LocalDateTime finalDate = initialDate.plus(Duration.of(Integer.parseInt(k)*Integer.parseInt(sa), ChronoUnit.MINUTES));
//        LocalDateTime finalDate = LocalDateTime.now();
        Algoritmo algoritmo = new Algoritmo(pedidoRepository,vehicleRepository);
        LocalDateTime finalDate = LocalDateTime.of(2022, Month.DECEMBER, 3, 0, 33, 0);
        algoritmo.listaPedidos = pedidoRepository.findLessThanDate(finalDate,0);
        algoritmo.inicializar();
        HashMap list = algoritmo.resolver();
        System.out.println("Rutas generadas...");
        return list;
    }
}
