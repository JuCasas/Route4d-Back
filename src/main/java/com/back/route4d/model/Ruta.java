package com.back.route4d.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Ruta")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Transient
    public List<Integer> recorrido;

    @Transient
    public List<Integer> retorno;

    @OneToMany(mappedBy="ruta")
    public List<Pedido> pedidos;

    @ManyToOne
    @JoinColumn(name = "idVehiculo", nullable = true)
    public Vehicle vehiculo;

    public int duracionMinutosRecorrido;
    public int duracionMinutosRetorno;
    public int tipoRuta;
    public int capacidad;
    public LocalDateTime plazoEntrega; // plazo entrega

    public LocalDateTime fechaInicioRecorrido;
    public LocalDateTime fechaInicioRetorno;
    public LocalDateTime fechaFinRetorno;

    public Ruta(){
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.plazoEntrega = LocalDateTime.of(2034,12,30,12,12);
        this.capacidad = 0;
        this.vehiculo = new Vehicle();
    }

    public Ruta(Vehicle vehiculo, int capacidad){
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.plazoEntrega = LocalDateTime.of(2034,12,30,12,12);
        this.capacidad = capacidad;
        this.vehiculo = vehiculo;
    }

    public void addPedido(Pedido pedido) {
        if(pedido.getFechaLimite().isBefore(plazoEntrega)) plazoEntrega = pedido.getFechaLimite();
        pedidos.add(pedido);
    }

    public void addNodo(int idNodo) {
        recorrido.add(idNodo);
    }

    public void addNodoRetorno(int idNodo) {
        retorno.add(idNodo);
    }
}
