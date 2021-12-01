package com.back.route4d.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Pedido")
public class Pedido implements Comparable<Pedido> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Transient
    private int idCluster;

    private int cluster;
    private int x;
    private int y;
    private int cantidad;
    private int minFaltantes;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaLimite;
    private int tiempoEntrega;
    private int tipoPedido;

    @ManyToOne
    @JoinColumn(name="idRuta", nullable=true)
    private Ruta ruta;

    @ManyToOne
    @JoinColumn(name="idVehiculo",nullable = true)
    private Vehicle vehicle;

    @Column(columnDefinition = "integer default 0")
    private int estado;

    public Pedido() {
    }

    public Pedido(int id, int x, int y, int cantidad, int minFaltantes) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.cantidad = cantidad;
        this.minFaltantes = minFaltantes;
    }

    public Pedido(int id, int x, int y, int cantidad, int minFaltantes, LocalDateTime fechaPedido,LocalDateTime fechaLimite, int tipoPedido) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.cantidad = cantidad;
        this.fechaLimite = fechaLimite;
        this.minFaltantes = minFaltantes;
        this.fechaPedido = fechaPedido;
        this.tipoPedido = tipoPedido;
    }

    public int getNodoId() {
        return this.x + 71 * this.y + 1;
    }

    @Override
    public String toString() {
        return this.getNodoId() + " Cantidad: " + this.cantidad + " Minutos Faltantes: " + this.minFaltantes;
    }

    @Override
    public int compareTo(Pedido p) {
        if (this.minFaltantes == p.minFaltantes) {
            if (this.cantidad == p.cantidad)
                return 0;
            else if (this.cantidad < p.cantidad)
                return 1;
            else
                return -1;
        } else if (this.minFaltantes > p.minFaltantes)
            return 1;
        else
            return -1;
    }
}
