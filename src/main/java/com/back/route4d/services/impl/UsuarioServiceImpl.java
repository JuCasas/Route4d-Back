package com.back.route4d.services.impl;

import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.Planta;
import com.back.route4d.model.Usuario;
import com.back.route4d.repository.UsuarioRepository;
import com.back.route4d.services.UsuarioService;
import com.back.route4d.services.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public Usuario mapPersistenceModelToRestModel(Usuario usuario) {
        Usuario usuarioM = new Usuario();

        usuarioM.setIdUsuario(usuario.getIdUsuario());
        usuarioM.setTipoUsuario(usuario.getTipoUsuario());
        usuarioM.setNombres(usuario.getNombres());
        usuarioM.setApellidos(usuario.getApellidos());
        usuarioM.setEmail(usuario.getEmail());
        usuarioM.setPassword(usuario.getPassword());
        usuarioM.setEstado(usuario.getEstado());
        return usuarioM;
    }
    @Override
    public Usuario getUsuarioById(int id) {
        return usuarioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuario", "Id", id));
    }
    @Override
    public Usuario patch(int idUsuario, Map<Object, Object> campos) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(
                ()-> new ResourceNotFoundException("Usuario","id",idUsuario));

        Usuario usuarioM = mapPersistenceModelToRestModel(usuario);
        campos.forEach(
                (campo, value) -> {
                    if("idUsuario".equals(campo)){
                        usuarioM.setIdUsuario((int) value);
                    }else if ("nombres".equals(campo)) {
                        usuarioM.setNombres((String)value);
                    } else if ("apellidos".equals(campo)) {
                        usuarioM.setApellidos((String)value);
                    } else if ("email".equals(campo)) {
                        usuarioM.setEmail((String)value);
                    } else if ("password".equals(campo)) {
                        usuarioM.setPassword((String) value);
                    }else if ("estado".equals(campo)) {
                        if(value.getClass().getName() == "java.lang.Integer"){
                            Integer aux = (Integer) value;
                            usuarioM.setEstado(aux);
                        }else{
                            usuarioM.setEstado(Integer.parseInt((String) value) );
                        }
                    }

                }
        );

        usuarioRepository.save(usuarioM);
        return usuarioM;
    }

    @Override
    public Usuario validateUsuario(String email, String password) {
        return usuarioRepository.findByEmailAddressAndPassword(email,password).orElseThrow(() ->
                new ResourceNotFoundException("Usuario y/o contraseña incorrecta"));
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