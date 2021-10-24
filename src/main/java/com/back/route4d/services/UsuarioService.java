package com.back.route4d.services;

import com.back.route4d.model.Usuario;

import java.util.List;

public interface UsuarioService {
    Usuario saveUsuario(Usuario usuario);
    List<Usuario> getAllUsuarios();
    Usuario getUsuarioById(int id);
    Usuario updateUsuario(Usuario usuario, int id);
    void deleteUsuario(int id);
}
