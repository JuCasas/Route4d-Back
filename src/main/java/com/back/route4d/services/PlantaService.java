package com.back.route4d.services;
import com.back.route4d.model.Planta;
import java.util.List;

public interface PlantaService {
    Planta savePlanta(Planta planta);
    List<Planta> getAllPlantas();
    Planta updatePlanta(Planta planta, int id);
    void deletePlanta(int id);
}
