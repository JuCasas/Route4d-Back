package com.back.route4d.services.impl;

import com.back.route4d.model.Averia;
import com.back.route4d.repository.AveriaRepository;
import com.back.route4d.services.AveriaService;
import org.springframework.stereotype.Service;

@Service
public class AveriaServiceImpl implements AveriaService {
    private AveriaRepository averiaRepository;

    public AveriaServiceImpl(AveriaRepository averiaRepository) {
        super();
        this.averiaRepository = averiaRepository;
    }

    @Override
    public Averia saveAveria(Averia averia) {
        return averiaRepository.save(averia);
    }
}
