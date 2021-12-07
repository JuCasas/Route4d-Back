package com.back.route4d.services;
import com.back.route4d.model.Usuario;
import com.back.route4d.model.Vehicle;

import java.util.List;
import java.util.Map;

public interface VehicleService {
    Vehicle saveVehicle(Map<String,String> nuevo);
    List<Vehicle> getAllVehicles();
    List<Vehicle> getAllType(int tipoId);
    Vehicle getVehicleById(int id);
    Vehicle patch(int id, Map<Object, Object> campos);
    Vehicle mapPersistenceModelToRestModel(Vehicle vehicle);
    Vehicle updateVehicle(Vehicle vehicle, int id);
    void deleteVehicle(int id);
    void averiarVehicle(int id);
}