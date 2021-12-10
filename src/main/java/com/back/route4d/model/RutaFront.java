package com.back.route4d.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class RutaFront implements Comparable<RutaFront>{
    public int id;
    public List<Map<String,Integer>>  recorrido;
    public List<Map<String,Integer>>  retorno;
    public List<Pedido> pedidos;
    public Vehicle vehiculo;
    public Integer tiempoFin;
    public int capacidad;
    public int consumoPetroleo;

    public int duracionMinutosRecorrido;
    public int duracionMinutosRetorno;
    public int duracion_minutos;

    public LocalDateTime plazoEntrega; // plazo entrega

    public LocalDateTime fechaInicioRecorrido;
    public LocalDateTime fechaInicioRetorno;
    public LocalDateTime fechaFinRetorno;


    //TODO cambiar el consumo de petroleo a Real
    public RutaFront(){
        this.id = 0;
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.tiempoFin = Integer.MAX_VALUE;
        this.capacidad = 0;
        this.consumoPetroleo = 20;
        this.vehiculo = new Vehicle();
    }

    public RutaFront(Vehicle vehiculo, int capacidad){
        this.id = 0;
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.tiempoFin = Integer.MAX_VALUE;
        this.capacidad = capacidad;
        this.consumoPetroleo = 20;
        this.vehiculo = new Vehicle();
        this.vehiculo = vehiculo;
    }

    @Override
    public int compareTo(RutaFront ruta) {
        return this.tiempoFin.compareTo(ruta.tiempoFin);
    }


}
