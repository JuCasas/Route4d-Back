package com.back.route4d.services.impl;

import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.Vehicle;
import com.back.route4d.repository.VehicleRepository;
import com.back.route4d.services.VehicleService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;


@Service
public class VehicleServiceImpl implements VehicleService {

    private VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        super();
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
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
        existingVehicle.setVelocidad(vehicle.getVelocidad());
        existingVehicle.setCapacidad(vehicle.getCapacidad());
        existingVehicle.setPeso(vehicle.getPeso());
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
