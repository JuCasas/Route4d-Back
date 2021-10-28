package com.back.route4d.model;

import java.util.ArrayList;
import java.util.List;

public class Ruta {
    public List<Integer> recorrido;
    public List<Integer> retorno;
    public List<Pedido> pedidos;
    public Vehicle vehiculo;
    public int tiempoMin;
    public Usuario chofer;
    public int capacidad;

    public Ruta(){
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.tiempoMin = Integer.MAX_VALUE;
        this.chofer = null;
        this.capacidad = 0;
        this.vehiculo = new Vehicle();
    }

    public Ruta(Vehicle vehiculo, int capacidad){
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.tiempoMin = Integer.MAX_VALUE;
        this.chofer = null;
        this.capacidad = capacidad;
        this.vehiculo = vehiculo;
    }

    public void addPedido(Pedido pedido) {
        if(pedido.getMinFaltantes() < tiempoMin) tiempoMin = pedido.getMinFaltantes();
        pedidos.add(pedido);
    }

    public void addNodo(int idNodo) {
        recorrido.add(idNodo);
    }

    public void addNodoRetorno(int idNodo) {
        retorno.add(idNodo);
    }
}
