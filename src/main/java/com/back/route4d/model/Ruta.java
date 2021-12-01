package com.back.route4d.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    public int duracionMinutos;
    public int tipoRuta;
    public int capacidad;
    public int plazoEntrega; // plazo entrega

    public LocalDateTime fechaInicioRecorrido;
    public LocalDateTime fechaInicioRetorno;
    public LocalDateTime fechaFinRetorno;

    public Ruta(){
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.plazoEntrega = Integer.MAX_VALUE;
        this.capacidad = 0;
        this.vehiculo = new Vehicle();
    }

    public Ruta(Vehicle vehiculo, int capacidad){
        this.recorrido = new ArrayList<>();
        this.retorno = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.plazoEntrega = Integer.MAX_VALUE;
        this.capacidad = capacidad;
        this.vehiculo = vehiculo;
    }

    public void addPedido(Pedido pedido) {
        if(pedido.getMinFaltantes() < plazoEntrega) plazoEntrega = pedido.getMinFaltantes();
        pedidos.add(pedido);
    }

    public void addNodo(int idNodo) {
        recorrido.add(idNodo);
    }

    public void addNodoRetorno(int idNodo) {
        retorno.add(idNodo);
    }
}
