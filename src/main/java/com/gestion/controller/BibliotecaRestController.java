package com.gestion.controller;


import com.gestion.entities.Biblioteca;
import com.gestion.repository.BibliotecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/biblioteca")
public class BibliotecaRestController {
    @Autowired
    BibliotecaRepository bibliotecaRepository;

    @GetMapping()
    public List<Biblioteca> list() {
        return bibliotecaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Biblioteca get(@PathVariable(name = "id") long id) {
        return bibliotecaRepository.findById(id).get();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Biblioteca input) {
        Biblioteca find =bibliotecaRepository.findById(id).get();
        if (find != null) {
            find.setNombre(input.getNombre());
            find.setDireccion(input.getDireccion());
            find.setTelefono(input.getTelefono());
            find.setEmail(input.getEmail());
            find.setSitioWeb(input.getSitioWeb());
        }
        Biblioteca save = bibliotecaRepository.save(find);
        return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Biblioteca input) {
        Biblioteca save = bibliotecaRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Biblioteca> findById = bibliotecaRepository.findById(id);
        if (findById.get() != null) {
            bibliotecaRepository.delete(findById.get());
        }
        return ResponseEntity.ok().build();
    }
}
