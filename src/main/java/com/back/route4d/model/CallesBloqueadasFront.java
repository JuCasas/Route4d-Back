package com.back.route4d.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class CallesBloqueadasFront {
    private final Integer minutosInicio;
    private final Integer minutosFin;
    public List<Map<String,Integer>> nodos;

    public CallesBloqueadasFront(int minutosInicio, int minutosFin) {
        this.minutosInicio = minutosInicio;
        this.minutosFin = minutosFin;
        this.nodos = new ArrayList<> ();
    }

}
