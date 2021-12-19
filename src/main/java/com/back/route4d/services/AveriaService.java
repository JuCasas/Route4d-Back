package com.back.route4d.services;

import com.back.route4d.model.Averia;

import java.util.List;

public interface AveriaService {
    Averia saveAveria(Averia averia);
    List<Averia> getAllAverias();
}
