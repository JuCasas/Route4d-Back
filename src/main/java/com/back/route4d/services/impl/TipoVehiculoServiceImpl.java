package com.back.route4d.services.impl;

import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.TipoVehiculo;
import com.back.route4d.repository.TipoVehiculoRepository;
import com.back.route4d.services.TipoVehiculoService;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void deleteTipoVehiculo(int id) {
        //Vehicle exists?
        tipoVehiculoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("TipoVehiculo","Id",id));

        tipoVehiculoRepository.deleteById(id);
    }
}
