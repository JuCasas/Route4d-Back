package com.back.route4d.tasks;

import com.back.route4d.services.AlgoritmoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PedidoTask {

    @Autowired
    private AlgoritmoService algoritmoService;

    @Scheduled(fixedRate = 180000, initialDelay = 180000)
    public void run() {
        try {

            System.out.println("Estoy corriendo...");
            algoritmoService.generarRutasBBDD();
            System.out.println("Estoy terminando...");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
