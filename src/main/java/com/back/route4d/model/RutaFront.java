package com.back.route4d.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class RutaFront implements Comparable<RutaFront>{
    public List<Map<String,Integer>>  recorrido;
    public List<Map<String,Integer>>  retorno;
    public List<Pedido> pedidos;
    public Vehicle vehiculo;
    public Integer tiempoMin;
    public int capacidad;

    public RutaFront(){
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.tiempoMin = Integer.MAX_VALUE;
        this.capacidad = 0;
        this.vehiculo = new Vehicle();
    }

    public RutaFront(Vehicle vehiculo, int capacidad){
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.tiempoMin = Integer.MAX_VALUE;
        this.capacidad = capacidad;
        this.vehiculo = new Vehicle();
        this.vehiculo = vehiculo;
    }

    @Override
    public int compareTo(RutaFront ruta) {
        return this.tiempoMin.compareTo(ruta.tiempoMin);
    }


}
