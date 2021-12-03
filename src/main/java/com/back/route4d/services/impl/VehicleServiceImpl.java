package com.back.route4d.services.impl;

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

    public Double convertirADouble(Object value){
        Double valor = null;
        if(value.getClass().getName().equals("java.lang.String")){
            valor = Double.parseDouble((String) value);
        }else if(value.getClass().getName().equals("java.lang.Integer")){
            Integer aux = (Integer) value;
            valor = new Double(aux);
        }
        return valor;
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
        vehicleRepository.save(existingVehicle);
        return existingVehicle;
    }

    @Override
    public Vehicle mapPersistenceModelToRestModel(Vehicle vehicle) {
        Vehicle vehicleM = new Vehicle();
        vehicleM.setPlaca(vehicle.getPlaca());
        vehicleM.setIdVehiculo(vehicle.getIdVehiculo());
        vehicleM.setTipo(vehicle.getTipo());
        vehicleM.setCapacidadActual(vehicle.getCapacidadActual());
        vehicleM.setEstado(vehicle.getEstado());
        return vehicleM;
    }

    @Override
    public Vehicle patch(int vehicleId, Map<Object, Object> campos) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(
                ()-> new ResourceNotFoundException("Vehicle","Id",vehicleId));



        Vehicle vehicleM = mapPersistenceModelToRestModel(vehicle);

        campos.forEach(
                (campo, value) -> {
                    if("idVehiculo".equals(campo)){
                        vehicleM.setIdVehiculo((int) value);
                    }else if ("estado".equals(campo)) {
                        if(value.getClass().getName().equals("java.lang.Integer")){
                            Integer aux = (Integer) value;
                            vehicleM.setEstado(aux);
                        }else{
                            vehicleM.setEstado(Integer.parseInt((String) value) );
                        }
                    } else if ("capacidadActual".equals(campo)) {
                        if(!value.getClass().getName().equals("java.lang.Double")){
                            Double nuevo = convertirADouble(value);
                            vehicleM.setCapacidadActual(nuevo);
                        }else{
                            vehicleM.setCapacidadActual((Double) value);
                        }
                    } else if ("placa".equals(campo)) {
                        vehicleM.setPlaca((String) value);
                    } else if ("idTipo".equals(campo)) {
                        TipoVehiculo tipo = tipoVehiculoRepository.findById((Integer) value).orElseThrow(
                                ()-> new ResourceNotFoundException("Vehicle","Id",vehicle.getTipo().getIdTipo()));
                        vehicleM.setCapacidadActual(tipo.getCapacidad());
                        vehicleM.setTipo(tipo);
                    }
                }
        );

        vehicleRepository.save(vehicleM);
        return vehicleM;
    }

    @Override
    public void deleteVehicle(int id) {
        //Vehicle exists?
        vehicleRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Vehicle","Id",id));

        vehicleRepository.deleteById(id);
    }
}