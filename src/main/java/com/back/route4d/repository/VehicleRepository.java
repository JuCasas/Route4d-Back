package com.back.route4d.repository;

import com.back.route4d.model.Usuario;
import com.back.route4d.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface VehicleRepository extends JpaRepository<Vehicle,Integer> {
    @Query(value = "select * from route4d.vehicle V, route4d.tipo_vehiculo TV\n" +
            "where V.id_tipo = TV.id_tipo and V.estado = 1 and V.id_tipo = ?1;", nativeQuery = true)
    ArrayList<Vehicle> getAllByType(int tipoId);
}
