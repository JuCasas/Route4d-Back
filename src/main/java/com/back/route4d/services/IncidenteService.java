package com.back.route4d.services;

import com.back.route4d.model.Incidente;


public interface IncidenteService {
    Incidente saveIncidente(Incidente incidente);
    Incidente getIncidenteById(int id);
}
