package com.back.route4d.repository;

import com.back.route4d.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido,Integer> {
    @Query(value = "select * from route4d.pedido \n" +
            "where fecha_pedido between ?1 and ?2 and estado = ?3 ",nativeQuery = true)
    List<Pedido> findByDate(LocalDateTime inicio, LocalDateTime fin, int estado);
}
