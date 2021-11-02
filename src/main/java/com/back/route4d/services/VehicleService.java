package com.back.route4d.services;

import com.back.route4d.model.Vehicle;

import java.util.List;

public interface VehicleService {
    Vehicle saveVehicle(Vehicle vehicle);
    List<Vehicle> getAllVehicles();
    List<Vehicle> getAllType(int tipoId);
    Vehicle getVehicleById(int id);
    Vehicle updateVehicle(Vehicle vehicle, int id);
    void deleteVehicle(int id);
}
