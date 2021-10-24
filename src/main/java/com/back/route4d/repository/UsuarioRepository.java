package com.back.route4d.repository;

import com.back.route4d.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    @Query(value = "SELECT * FROM route4d.usuario U WHERE U.email = ?1 AND U.password = ?2", nativeQuery = true)
    Optional<Usuario> findByEmailAddressAndPassword(String email, String password);
}
