package com.back.route4d.tasks;

public class PedidoTask implements Runnable {
    @Override
    public void run() {
        try{
            System.out.println("Estoy corriendo...");
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
