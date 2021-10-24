package com.back.route4d.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idIncidente;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private String descripcion;

    //@ManyToOne
    //@JoinColumn(name="idRuta",nullable = false)
    //private Ruta ruta;

    @ManyToOne
    @JoinColumn(name="idVehiculo",nullable = false)
    private  Vehicle vehicle;
}
