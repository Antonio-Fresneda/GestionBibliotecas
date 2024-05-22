package com.gestion.controller;


import com.gestion.entities.Libro;
import com.gestion.entities.Permisos;
import com.gestion.repository.PermisosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permisos")
public class PermisosController {

    @Autowired
    PermisosRepository permisosRepository;

    @GetMapping()
    public List<Permisos> list() {
        return permisosRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Permisos input) {
        Permisos save = permisosRepository.save(input);
        return ResponseEntity.ok((save));
    }
}
