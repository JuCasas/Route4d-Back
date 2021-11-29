package com.back.route4d.services.impl;

import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.Planta;
import com.back.route4d.repository.PlantaRepository;
import com.back.route4d.services.PlantaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


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
    public Planta mapPersistenceModelToRestModel(Planta planta) {
        Planta plantaM = new Planta();

        plantaM.setIdPlantas(planta.getIdPlantas());
        plantaM.setCapacidad(planta.getCapacidad());
        plantaM.setX(planta.getX());
        plantaM.setY(planta.getY());
        plantaM.setTipo(planta.getTipo());
        return plantaM;
    }

    @Override
    public Planta patch(int idPlanta, Map<Object, Object> campos) {
        Planta planta = plantaRepository.findById(idPlanta).orElseThrow(
                ()-> new ResourceNotFoundException("Planta","id",idPlanta));

        Planta plantaM = mapPersistenceModelToRestModel(planta);

        campos.forEach(
                (campo, value) -> {
                    if("idPlanta".equals(campo)){
                        plantaM.setIdPlantas((int) value);
                    }else if ("capacidad".equals(campo)) {
                        plantaM.setCapacidad((int) value);
                    } else if ("tipo".equals(campo)) {
                        plantaM.setTipo((int) value);
                    } else if ("x".equals(campo)) {
                        plantaM.setX((int) value);
                    } else if ("y".equals(campo)) {
                        plantaM.setY((int) value);
                    }
                }
        );

        plantaRepository.save(plantaM);
        return plantaM;
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
