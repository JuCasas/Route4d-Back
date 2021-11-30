package com.back.route4d.tasks;

import com.back.route4d.algoritmo.Algoritmo;
import com.back.route4d.services.AlgoritmoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

public class PedidoTask implements Runnable {

    @Autowired
    private AlgoritmoService algoritmoService;
    public PedidoTask() {

    }

    @Override
    public void run() {
        try{
            System.out.println("Estoy corriendo...");
            algoritmoService.enviarRutasOperacion("s","s");
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
