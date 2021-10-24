package com.back.route4d.services;

import com.back.route4d.model.TipoVehiculo;

import java.util.List;
public interface TipoVehiculoService {
    TipoVehiculo saveTipoVehiculo(TipoVehiculo tipo);
    List<TipoVehiculo> getAllTipoVehiculo();
    TipoVehiculo getTipoById(int id);
    TipoVehiculo updateTipoVehiculo(TipoVehiculo tipo, int id);
    void deleteTipoVehiculo(int id);
}
