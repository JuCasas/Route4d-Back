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
    private final Integer duracionMinutos;
    public List<Map<String,Integer>> nodos;

    public CallesBloqueadasFront(LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer duracionMinutos) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.duracionMinutos = duracionMinutos;
        this.nodos = new ArrayList<> ();
    }

}
