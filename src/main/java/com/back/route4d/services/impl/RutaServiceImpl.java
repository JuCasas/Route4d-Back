package com.back.route4d.services.impl;

import com.back.route4d.model.Ruta;
import com.back.route4d.repository.RutaRepository;
import com.back.route4d.services.RutaService;
import org.springframework.stereotype.Service;

@Service
public class RutaServiceImpl implements RutaService {

    private RutaRepository rutaRepository;

    public RutaServiceImpl(RutaRepository rutaRepository) {
        super();
        this.rutaRepository = rutaRepository;
    }

    @Override
    public Ruta saveRuta(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

}
