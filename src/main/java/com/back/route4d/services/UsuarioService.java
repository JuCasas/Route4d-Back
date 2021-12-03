package com.back.route4d.services;

import com.back.route4d.model.Planta;
import com.back.route4d.model.Usuario;

import java.util.List;
import java.util.Map;

public interface UsuarioService {
    Usuario saveUsuario(Usuario usuario);
    List<Usuario> getAllUsuarios();
    Usuario getUsuarioById(int id);
    Usuario validateUsuario(String email, String password);
    Usuario patch(int id, Map<Object, Object> campos);
    Usuario mapPersistenceModelToRestModel(Usuario tipo);
    Usuario updateUsuario(Usuario usuario, int id);
    void deleteUsuario(int id);
}
