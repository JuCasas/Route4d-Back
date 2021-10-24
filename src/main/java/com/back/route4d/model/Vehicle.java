package com.back.route4d.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idVehiculo;

    @Column(nullable = false)
    private String placa;

    @Column(nullable = false)
    private double capacidadActual;

    @ManyToOne
    @JoinColumn(name="idTipo",nullable = false)
    private TipoVehiculo tipo;

    @Column(nullable = false)
    private int estado;

}
