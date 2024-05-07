package com.gestion.controller;


import com.gestion.dto.UsuarioDto;
import com.gestion.dto.UsuarioLoginDto;
import com.gestion.entities.Autor;
import com.gestion.entities.Rol;
import com.gestion.entities.Usuario;
import com.gestion.repository.RolRepository;
import com.gestion.repository.UsuarioRepository;
import com.gestion.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UsuarioLoginDto usuarioLogin, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>("El usuario y la clave son obligatorios", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<UsuarioDto>(usuarioService.login(usuarioLogin), HttpStatus.OK);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioDto usuario, BindingResult validaciones)
            throws Exception {
        if (validaciones.hasErrors()) {
            return new ResponseEntity<String>("Campos Imcompletos", HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<UsuarioDto>(usuarioService.crear(usuario), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
