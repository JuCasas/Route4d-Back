package com.back.route4d.services.impl;

import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.Usuario;
import com.back.route4d.repository.UsuarioRepository;
import com.back.route4d.services.UsuarioService;
import com.back.route4d.services.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super();
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario getUsuarioById(int id) {
        return usuarioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuario", "Id", id));
    }

    @Override
    public Usuario validateUsuario(String email, String password) {
        return usuarioRepository.findByEmailAddressAndPassword(email,password).orElseThrow(() ->
                new ResourceNotFoundException());
    }

    @Override
    public Usuario updateUsuario(Usuario usuario, int id) {
        //User exists?
        Usuario existingUsuario = usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario", "Id", id));

        existingUsuario.setNombres(usuario.getNombres());
        existingUsuario.setApellidos(usuario.getApellidos());
        existingUsuario.setEmail(usuario.getEmail());
        existingUsuario.setPassword(usuario.getPassword());
        existingUsuario.setEstado(usuario.getEstado());
        existingUsuario.setTipoUsuario(usuario.getTipoUsuario());

        //Save vehicle to DB
        usuarioRepository.save(existingUsuario);
        return existingUsuario;
    }

    @Override
    public void deleteUsuario(int id) {
        //Usuario exists?
        usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario", "Id", id));

        usuarioRepository.deleteById(id);
    }
}