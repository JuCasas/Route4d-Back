package com.back.route4d.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
@Table(name = "Pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int cluster;
    private int x;
    private int y;
    private int cantidad;
    private int minFaltantes;
    private Date fechaPedido;
    private Date fechaLimite;
    private int tiempoEntrega;

}
