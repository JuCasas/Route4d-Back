package com.back.route4d.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "TipoVehiculo")
public class TipoVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipo;

    @Column(nullable = false)
    private double pesoBruto;

    @Column(nullable = true)
    private String nombre;

    @Column(nullable = false)
    private int estado;

    @Column(nullable = false)
    private double capacidad;

    @Column(nullable = false)
    private double pesoCarga;

    @Column(nullable = false)
    private double velocidad;

    @Column(nullable = false)
    private double cantidad;


    public TipoVehiculo() {
        super();
    }
}
