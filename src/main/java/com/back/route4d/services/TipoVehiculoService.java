package com.back.route4d.services;

import com.back.route4d.model.TipoVehiculo;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface TipoVehiculoService {
    TipoVehiculo saveTipoVehiculo(TipoVehiculo tipo);
    List<TipoVehiculo> getAllTipoVehiculo();
    TipoVehiculo getTipoById(int id);
    TipoVehiculo patch(int tipoId, Map<Object, Object> campos);
    TipoVehiculo updateTipoVehiculo(TipoVehiculo tipo, int id);
    TipoVehiculo mapPersistenceModelToRestModel(TipoVehiculo tipo);
    void deleteTipoVehiculo(int id);
}
