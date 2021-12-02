package com.back.route4d.repository;

import com.back.route4d.model.Usuario;
import com.back.route4d.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle,Integer> {
    @Query(value = "select * from route4d.vehicle V, route4d.tipo_vehiculo TV \n" +
            "where V.id_tipo = TV.id_tipo and V.estado = 1 and V.id_tipo = ?1", nativeQuery = true)
    List<Vehicle> getAllByType(int tipoId);

    @Query(value = "select p.* from route4d.vehicle as p, route4d.ruta as r\n" +
            "where p.id_vehiculo = r.id_vehiculo and not(\n" +
            "r.fecha_inicio_recorrido < ?1 \n" +
            "and r.fecha_fin_retorno > ?1 and \n" +
            "p.id_tipo = ?2;",nativeQuery = true)
    List<Vehicle> getAvailableByType(LocalDateTime tiempo, int tipoId);
}
