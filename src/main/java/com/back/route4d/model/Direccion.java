package com.back.route4d.model;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDireccion;

    @Column(nullable = false)
    private int coordenadaX;

    @Column(nullable = false)
    private int coordenadaY;

    @Column(nullable = false)
    private String descripcion;

    //@ManyToOne
    //@JoinColumn(name="idPedido",nullable = false)
    //private Pedido pedido;

}
