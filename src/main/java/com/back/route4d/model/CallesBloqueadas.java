package com.back.route4d.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;

@Data
@Entity
@Table(name = "CallesBloqueadas")
public class CallesBloqueadas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int minutosInicio;
    private int minutosFin;

    @Transient
    private HashSet<Integer> conjuntoNodos;

    private String nodos;

    public CallesBloqueadas() {
    }

    public CallesBloqueadas(int id, int minutosInicio, int minutosFin) {
        this.id = id;
        this.minutosInicio = minutosInicio;
        this.minutosFin = minutosFin;
        this.conjuntoNodos = new HashSet<>();
        this.nodos = "";
    }

    public void addNode(int nodoId) {
        conjuntoNodos.add(nodoId);
    }

    public void addNodeToString(int nodoId) {
        nodos += nodoId + ",";
    }

    public boolean estaNodo(int nodoId) {
        return conjuntoNodos.contains(nodoId);
    }
}
