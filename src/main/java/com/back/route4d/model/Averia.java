package com.back.route4d.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Averia")
public class Averia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAveria;

    @ManyToOne
    @JoinColumn(name = "idVehiculo", nullable = false)
    private Vehicle vehicle;

    private LocalDateTime fechaAveria;

    public Averia() {
    }
}
