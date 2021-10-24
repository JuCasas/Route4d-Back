package com.back.route4d.services.impl;
import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.Incidente;
import com.back.route4d.repository.IncidenteRepository;
import com.back.route4d.services.IncidenteService;
import org.springframework.stereotype.Service;

@Service
public class IncidenteServiceImpl  implements IncidenteService {
    private IncidenteRepository incidenteRepository;

    public IncidenteServiceImpl(IncidenteRepository incidenteRepository) {
        super();
        this.incidenteRepository = incidenteRepository;
    }

    @Override
    public Incidente saveIncidente(Incidente incidente) {
        return incidenteRepository.save(incidente);
    }


    @Override
    public Incidente getIncidenteById(int id) {
        return incidenteRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Incidente","Id",id));
    }

}
