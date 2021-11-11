package com.back.route4d.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Planta")
public class Planta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPlantas;

    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    @Column(nullable = false)
    private int tipo;

    @Column(nullable = false)
    private int capacidad;

    public Planta() {
    }
}
