package com.gestion.controller;


import com.gestion.dto.LibroDto;
import com.gestion.dto.UsuarioDto;
import com.gestion.dto.UsuarioLoginDto;

import com.gestion.entities.*;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.RolRepository;
import com.gestion.repository.UsuarioRepository;
import com.gestion.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuario")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping()
    public List<UsuarioDto> list() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody UsuarioDto input) throws Exception {
        Usuario find = usuarioRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNombre(input.getNombre());
            find.setApellidos(input.getApellidos());
            find.setTelefono(input.getTelefono());
            find.setEmail(input.getEmail());
            find.setFechaNacimiento(input.getFechaNacimiento());

            Rol rol = rolRepository.findByNombreRol(input.getRol()).orElseThrow(() ->
                    new Exception("No existe el rol en la base de datos, inserte primero"));
            find.setRol(rol);

        } else {
            throw new BibliotecaNotFoundException("Usuario not found with id: " + id);
        }
        Usuario save = usuarioRepository.save(find);
        return ResponseEntity.ok(save);
    }


    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuarioRepository.delete(usuario);
            return ResponseEntity.ok().build();
        } else {
            throw new BibliotecaNotFoundException("Usuario not found with id: " + id);
        }
    }

    private UsuarioDto convertToDto(Usuario usuario) {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setEmail(usuario.getEmail());
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setApellidos(usuario.getApellidos());
        usuarioDto.setTelefono(usuario.getTelefono());
        usuarioDto.setFechaNacimiento(usuario.getFechaNacimiento());
        usuarioDto.setRol(usuario.getRol().getNombreRol());
        return usuarioDto;
    }
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
