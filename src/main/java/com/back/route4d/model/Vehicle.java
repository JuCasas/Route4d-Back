package com.back.route4d.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String placa;

    @Column(nullable = false)
    private double capacidad;

    @Column(nullable = false)
    private double velocidad;

    @Column(nullable = false)
    private double peso;

}
