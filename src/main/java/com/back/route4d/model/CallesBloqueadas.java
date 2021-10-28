package com.back.route4d.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class CallesBloqueadas {
    private final Integer id;
    private final Integer minutosInicio;
    private final Integer minutosFin;
    private final HashSet<Integer> nodos;

    public CallesBloqueadas(Integer id, int minutosInicio, int minutosFin) {
        this.id = id;
        this.minutosInicio = minutosInicio;
        this.minutosFin = minutosFin;
        this.nodos = new HashSet<>();
    }

    public void addNode(int nodoId) {
        nodos.add(nodoId);
    }

    public boolean estaNodo(int nodoId) {
        return nodos.contains(nodoId);
    }
}
