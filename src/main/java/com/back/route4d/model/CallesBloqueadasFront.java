package com.back.route4d.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class CallesBloqueadasFront {
    private final LocalDateTime fechaInicio;
    private final LocalDateTime fechaFin;
    public List<Map<String,Integer>> nodos;

    public CallesBloqueadasFront(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.nodos = new ArrayList<> ();
    }

}
