package com.back.route4d.services;
import com.back.route4d.model.Planta;
import com.back.route4d.model.TipoVehiculo;

import java.util.List;
import java.util.Map;

public interface PlantaService {
    Planta savePlanta(Planta planta);
    List<Planta> getAllPlantas();
    Planta patch(int id, Map<Object, Object> campos);
    Planta mapPersistenceModelToRestModel(Planta tipo);
    Planta updatePlanta(Planta planta, int id);
    void deletePlanta(int id);
}
