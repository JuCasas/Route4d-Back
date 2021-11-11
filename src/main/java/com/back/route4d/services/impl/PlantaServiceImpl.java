package com.back.route4d.services.impl;

import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.Planta;
import com.back.route4d.model.TipoVehiculo;
import com.back.route4d.repository.PlantaRepository;
import com.back.route4d.services.PlantaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PlantaServiceImpl implements  PlantaService{

    private PlantaRepository plantaRepository;

    public PlantaServiceImpl(PlantaRepository plantaRepository) {
        super();
        this.plantaRepository = plantaRepository;
    }

    @Override
    public Planta savePlanta(Planta planta) {
        return plantaRepository.save(planta);
    }

    @Override
    public List<Planta> getAllPlantas() {
        return plantaRepository.findAll();
    }


    @Override
    public Planta updatePlanta(Planta planta, int id) {
        //Vehicle exists?
        Planta existingPlanta = plantaRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("TipoPlanta","Id",id));

        existingPlanta.setIdPlantas(planta.getIdPlantas());
        existingPlanta.setCapacidad(planta.getCapacidad());
        existingPlanta.setTipo(planta.getTipo());
        existingPlanta.setX(planta.getX());
        existingPlanta.setY(planta.getY());
        plantaRepository.save(existingPlanta);
        return existingPlanta;
    }


    @Override
    public void deletePlanta(int id) {
        //Planta exists?
        plantaRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("TipoPlanta","Id",id));

        plantaRepository.deleteById(id);
    }
}
