package com.back.route4d.services.impl;

import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.Vehicle;
import com.back.route4d.repository.VehicleRepository;
import com.back.route4d.services.VehicleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
//        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
//        if(vehicle.isPresent()){
//            return vehicle.get();
//        }else {
//            throw new ResourceNotFoundException("Vehicle","Id",id);
//        }
        return vehicleRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Vehicle","Id",id));
    }


}
