package com.back.route4d.repository;

import com.back.route4d.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    @Query(value = "SELECT r.*\n" +
            "FROM route4d.ruta r\n" +
            "JOIN route4d.vehicle v ON v.id_vehiculo = r.id_vehiculo and v.id_tipo = ?1\n" +
            "WHERE r.plazo_entrega IN(SELECT MAX(r.plazo_entrega)\n" +
            "FROM route4d.ruta r\n" +
            "JOIN route4d.vehicle v ON v.id_vehiculo = r.id_vehiculo\n" +
            "GROUP BY r.id_vehiculo);",nativeQuery = true)
    List<Ruta> getRoutesByTypeId(int tipoId);
}