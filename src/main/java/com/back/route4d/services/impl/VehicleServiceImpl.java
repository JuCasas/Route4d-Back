package com.back.route4d.services.impl;

import com.back.route4d.controller.VehicleController;
import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.Vehicle;
import com.back.route4d.repository.TipoVehiculoRepository;
import com.back.route4d.repository.VehicleRepository;
import com.back.route4d.services.VehicleService;
import org.springframework.stereotype.Service;
import com.back.route4d.model.TipoVehiculo;

import java.util.List;
import java.util.Map;



@Service
public class VehicleServiceImpl implements VehicleService {

    private VehicleRepository vehicleRepository;
    private TipoVehiculoRepository tipoVehiculoRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,TipoVehiculoRepository tipoVehiculoRepository) {
        super();
        this.vehicleRepository = vehicleRepository;
        this.tipoVehiculoRepository=tipoVehiculoRepository;
    }

    @Override
    public Vehicle saveVehicle(Map<String,String> nuevo) {
        String aux = nuevo.get("idTipo");
        int id = Integer.parseInt(aux);
        String pl = nuevo.get("placa");
        TipoVehiculo tipo = tipoVehiculoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("TipoVehiculo","Id",id));

        Vehicle vehicle = new Vehicle();

        vehicle.setPlaca(pl);
        vehicle.setCapacidadActual(tipo.getCapacidad());
        vehicle.setTipo(tipo);

        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> getAllType(int tipoId) {
        return vehicleRepository.getAllByType(tipoId);
    }

    @Override
    public Vehicle getVehicleById(int id) {
        return vehicleRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Vehicle","Id",id));
    }

    @Override
    public Vehicle updateVehicle(Vehicle vehicle, int id) {
        //Vehicle exists?
        Vehicle existingVehicle = vehicleRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Vehicle","Id",id));

        existingVehicle.setPlaca(vehicle.getPlaca());
        existingVehicle.setTipo(vehicle.getTipo());
        existingVehicle.setCapacidadActual(vehicle.getCapacidadActual());
        existingVehicle.setEstado(vehicle.getEstado());
        //Save vehicle to DB
        vehicleRepository.save(existingVehicle);
        return existingVehicle;
    }

    @Override
    public void deleteVehicle(int id) {
        //Vehicle exists?
        vehicleRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Vehicle","Id",id));

        vehicleRepository.deleteById(id);
    }


}