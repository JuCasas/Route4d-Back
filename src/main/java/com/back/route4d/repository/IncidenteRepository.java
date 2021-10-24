package com.back.route4d.repository;

import com.back.route4d.model.Incidente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface IncidenteRepository extends JpaRepository<Incidente,Integer> {
}
