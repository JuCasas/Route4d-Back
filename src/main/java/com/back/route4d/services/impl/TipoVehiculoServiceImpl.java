package com.back.route4d.services.impl;

import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.TipoVehiculo;
import com.back.route4d.repository.TipoVehiculoRepository;
import com.back.route4d.services.TipoVehiculoService;
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

@Service
public class TipoVehiculoServiceImpl implements TipoVehiculoService {

    private TipoVehiculoRepository tipoVehiculoRepository;

    public TipoVehiculoServiceImpl(TipoVehiculoRepository tipoVehiculoRepository) {
        super();
        this.tipoVehiculoRepository = tipoVehiculoRepository;
    }

    @Override
    public TipoVehiculo saveTipoVehiculo(TipoVehiculo tipo) {
        return tipoVehiculoRepository.save(tipo);
    }

    @Override
    public List<TipoVehiculo> getAllTipoVehiculo() {
        return tipoVehiculoRepository.findAll();
    }

    @Override
    public TipoVehiculo getTipoById(int id) {
        return tipoVehiculoRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("TipoVehiculo","Id",id));
    }

    @Override
    public TipoVehiculo updateTipoVehiculo(TipoVehiculo tipo, int id) {
        //Vehicle exists?
        TipoVehiculo existingTipo = tipoVehiculoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("TipoVehiculo","Id",id));


        existingTipo.setPesoBruto(tipo.getPesoBruto());
        existingTipo.setCapacidad(tipo.getCapacidad());
        existingTipo.setPesoCarga(tipo.getPesoCarga());
        existingTipo.setVelocidad(tipo.getVelocidad());
        existingTipo.setCantidad(tipo.getCantidad());
        existingTipo.setNombre(tipo.getNombre());
        existingTipo.setEstado(tipo.getEstado());
        //Save vehicle to DB
        tipoVehiculoRepository.save(existingTipo);
        return existingTipo;
    }

    @Override
    public TipoVehiculo mapPersistenceModelToRestModel(TipoVehiculo tipo) {
        TipoVehiculo tipoM = new TipoVehiculo();

        tipoM.setIdTipo(tipo.getIdTipo());
        tipoM.setCapacidad(tipo.getCapacidad());
        tipoM.setPesoCarga(tipo.getPesoCarga());
        tipoM.setVelocidad(tipo.getVelocidad());
        tipoM.setCantidad(tipo.getCantidad());
        tipoM.setPesoBruto(tipo.getPesoBruto());
        tipoM.setNombre(tipo.getNombre());
        tipoM.setEstado(tipo.getEstado());
        return tipoM;
    }

    @Override
    public TipoVehiculo patch(int tipoId, Map<Object, Object> campos) {
        TipoVehiculo tipo = tipoVehiculoRepository.findById(tipoId).orElseThrow(
                ()-> new ResourceNotFoundException("TipoVehiculo","Id",tipoId));
//        Optional<TipoVehiculo> tipo = Optional.ofNullable(tipoVehiculoRepository.findById(tipoId).orElseThrow(
//                () -> new ResourceNotFoundException("TipoVehiculo", "Id", tipoId)));


        TipoVehiculo tipoM = mapPersistenceModelToRestModel(tipo);

        campos.forEach(
                (campo, value) -> {
                    if ("cantidad".equals(campo)) {
                        tipoM.setCantidad((double) value);
                    } else if ("velocidad".equals(campo)) {
                        tipoM.setVelocidad((double) value);
                    } else if ("pesoCarga".equals(campo)) {
                        tipoM.setPesoCarga((double) value);
                    } else if ("capacidad".equals(campo)) {
                        tipoM.setCapacidad((double) value);
                    } else if ("nombre".equals(campo)) {
                        tipoM.setNombre((String) value);
                    } else if ("estado".equals(campo)) {
                        tipoM.setEstado((int) value);
                    } else if ("pesoBruto".equals(campo)) {
                        tipoM.setPesoBruto((double) value);
                    }
                }
        );

        tipoVehiculoRepository.save(tipoM);
        return tipoM;
    }

    @Override
    public void deleteTipoVehiculo(int id) {
        //Vehicle exists?
        tipoVehiculoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("TipoVehiculo","Id",id));

        tipoVehiculoRepository.deleteById(id);
    }
}
