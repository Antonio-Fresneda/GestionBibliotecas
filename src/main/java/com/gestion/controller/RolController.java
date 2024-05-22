package com.gestion.controller;


;import com.gestion.dto.LibroDto;
import com.gestion.dto.RolDto;
import com.gestion.entities.*;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/rol")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @GetMapping()
    public List<RolDto> list() {
        List<Rol> roles = rolRepository.findAll();
        List<RolDto> rolDtos = new ArrayList<>();
        for (Rol rol : roles) {
            rolDtos.add(convertToDto(rol));
        }
        return rolDtos;
    }


    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Rol> rolOptional = rolRepository.findById(id);
        if (rolOptional.isPresent()) {
            Rol rol = rolOptional.get();
            rolRepository.delete(rol);
            return ResponseEntity.ok().build();
        } else {
            throw new BibliotecaNotFoundException("Rol not found with id: " + id);
        }
    }
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Rol input) {
        Rol save = rolRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Rol input) {
        Rol find = rolRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNombreRol(input.getNombreRol());
            find.setPermisos(input.getPermisos());

        } else {
            throw new BibliotecaNotFoundException("Usuario not found with id: " + id);
        }
        Rol save = rolRepository.save(find);
        return ResponseEntity.ok(save);
    }

    private RolDto convertToDto(Rol rol) {
        RolDto rolDto = new RolDto();
        rolDto.setId(rol.getId());
        rolDto.setNombre(rol.getNombreRol());

        rolDto.setPermisos(rol.getPermisos().toString());

        Set<Permisos> permisos = rol.getPermisos();

        List<String> permisosRol = new ArrayList<>();

        for (Permisos permiso : permisos) {
            permisosRol.add(permiso.getNombre());
        }

        String titulosGenerosString = String.join(", ", permisosRol);

        rolDto.setPermisos(titulosGenerosString);



        return rolDto;
    }

}
