package com.back.route4d.tasks;

import com.back.route4d.services.AlgoritmoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PedidoTask {

    @Autowired
    private AlgoritmoService algoritmoService;

//    @Scheduled(fixedRate = 40000, initialDelay = 40000)
    public void run() {
        try {

            System.out.println("Estoy corriendo...");
            algoritmoService.enviarRutasOperacion("s","s");
            System.out.println("Estoy terminando...");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
