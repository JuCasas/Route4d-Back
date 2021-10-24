package com.back.route4d.controller;

import com.back.route4d.model.Usuario;
import com.back.route4d.services.UsuarioService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        super();
        this.usuarioService = usuarioService;
    }

    //Build create usuario REST API
    @PostMapping("/")
    public ResponseEntity<Usuario> saveUsuario(@RequestBody Usuario usuario){
        return new ResponseEntity<Usuario>(usuarioService.saveUsuario(usuario), HttpStatus.CREATED);
    }

    @PostMapping("/validate")
    public ResponseEntity<Usuario> validateUsuario(@RequestBody Map<String, String> json){
        return new ResponseEntity<Usuario>(usuarioService.validateUsuario(json.get("email"), json.get("password")), HttpStatus.OK);
    }

    //Build get all Usuarios REST API
    @GetMapping
    public List<Usuario> getAllUsuarios(){
        return usuarioService.getAllUsuarios();
    }

    //Build get Usuario by ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable("id") int usuarioId){
        return new ResponseEntity<Usuario>(usuarioService.getUsuarioById(usuarioId),HttpStatus.OK);
    }

    //Build update usuario by ID
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable("id") int usuarioId, @RequestBody Usuario usuario){
        return new ResponseEntity<Usuario>(usuarioService.updateUsuario(usuario,usuarioId),HttpStatus.OK);
    }

    //Build delete Usuario by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsuario(@PathVariable("id") int usuarioId){
        usuarioService.deleteUsuario(usuarioId);
        return new ResponseEntity<String>("Usuario eliminado correctamente!",HttpStatus.OK);
    }


}
