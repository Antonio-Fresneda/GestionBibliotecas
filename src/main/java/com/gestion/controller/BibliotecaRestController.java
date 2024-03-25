package com.gestion.controller;


import com.gestion.dto.BibliotecaDto;
import com.gestion.entities.*;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/biblioteca")
public class BibliotecaRestController {

    @Autowired
    BibliotecaRepository bibliotecaRepository;


    @Autowired
    AutorRepository autorRepository;

    @Autowired
    LibroRepository libroRepository;

    @Autowired
    GeneroRepository generoRepository;

    @Autowired
    BibliotecaLibroRepository bibliotecaLibroRepository;


    @GetMapping()
    public List<BibliotecaDto> getAll() {
        List<Biblioteca> bibliotecas = bibliotecaRepository.findAll();
        return bibliotecas.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BibliotecaDto get(@PathVariable(name = "id") long id) {
        Biblioteca biblioteca = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new BibliotecaNotFoundException("Biblioteca not found with id: " + id));
        return convertToDto(biblioteca);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody BibliotecaDto input) {
        Biblioteca biblioteca = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new BibliotecaNotFoundException("Biblioteca not found with id: " + id));
        biblioteca.setNombre(input.getNombre());
        biblioteca.setDireccion(input.getDireccion());
        biblioteca.setTelefono(input.getTelefono());
        biblioteca.setEmail(input.getEmail());
        biblioteca.setSitioWeb(input.getSitioWeb());
        Biblioteca savedBiblioteca = bibliotecaRepository.save(biblioteca);
        return ResponseEntity.ok(convertToDto(savedBiblioteca));
    }

    @PostMapping("/crear")
    public ResponseEntity<Biblioteca> crearBiblioteca(@RequestBody Biblioteca biblioteca) {
        if (biblioteca.getId() == null || biblioteca.getId() == 0) {
            biblioteca = bibliotecaRepository.save(biblioteca);
        }

        for (BibliotecaLibro bibliotecaLibro : biblioteca.getLibroBibliotecas()) {
            Libro libro = bibliotecaLibro.getLibro();
            Libro existingLibro = libroRepository.findByTitulo(libro.getTitulo());

            if (existingLibro == null) {
                // Si no existe, se intenta crear un nuevo libro
                Autor autor = libro.getAutor();
                if (autor != null && (autor.getId() == null || autor.getId() == 0)) {
                    autor = autorRepository.save(autor);
                }

                for (LibroGenero libroGenero : libro.getGeneros()) {
                    Genero genero = libroGenero.getGenero();
                    if (genero != null && (genero.getId() == null || genero.getId() == 0)) {
                        genero = generoRepository.save(genero);
                    } else if (genero != null) {
                        Optional<Genero> existingGenero = generoRepository.findById(genero.getId());
                        if (existingGenero.isEmpty()) {
                            genero = generoRepository.save(genero);
                        }
                    }
                }

                libro = libroRepository.save(libro); // Se guarda el nuevo libro en la base de datos
            } else {
                // Si el libro existe, se usa el libro existente
                libro = existingLibro;
            }

            if (bibliotecaLibro.getId() == null || bibliotecaLibro.getId() == 0) {
                bibliotecaLibro = bibliotecaLibroRepository.save(bibliotecaLibro);
            }

            bibliotecaLibro.setLibro(libro);
            bibliotecaLibro.setBiblioteca(biblioteca);
        }

        biblioteca = bibliotecaRepository.save(biblioteca);

        return new ResponseEntity<>(biblioteca, HttpStatus.CREATED);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        bibliotecaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filtros")
    public Page<BibliotecaDto> busquedaDinamica(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "direccion", required = false) String direccion,
            @RequestParam(name = "telefono", required = false) String telefono,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "sitioWeb", required = false) String sitioWeb,
            @RequestParam(name = "direccion", defaultValue = "asc") String direccionOrden,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanoPagina", defaultValue = "10") int tamanoPagina) {

        Sort.Direction direction = Sort.Direction.ASC;
        if (direccionOrden.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        PageRequest pageRequest = PageRequest.of(pagina, tamanoPagina, Sort.by(direction, "nombre"));

        if (nombre != null && !nombre.isEmpty()) {
            if (direccion != null && !direccion.isEmpty()) {
                if (telefono != null && !telefono.isEmpty()) {
                    if (email != null && !email.isEmpty()) {
                        if (sitioWeb != null && !sitioWeb.isEmpty()) {
                            return bibliotecaRepository.findAllByNombreContainingAndDireccionContainingAndTelefonoContainingAndEmailContainingAndSitioWebContaining(nombre, direccion, telefono, email, sitioWeb, pageRequest)
                                    .map(this::convertToDto);
                        } else {
                            return bibliotecaRepository.findAllByNombreContainingAndDireccionContainingAndTelefonoContainingAndEmailContaining(nombre, direccion, telefono, email, pageRequest)
                                    .map(this::convertToDto);
                        }
                    } else {
                        return bibliotecaRepository.findAllByNombreContainingAndDireccionContainingAndTelefonoContaining(nombre, direccion, telefono, pageRequest)
                                .map(this::convertToDto);
                    }
                } else {
                    return bibliotecaRepository.findAllByNombreContainingAndDireccionContaining(nombre, direccion, pageRequest)
                            .map(this::convertToDto);
                }
            } else {
                return bibliotecaRepository.findAllByNombreContaining(nombre, pageRequest)
                        .map(this::convertToDto);
            }
        } else {
            return bibliotecaRepository.findAll(pageRequest)
                    .map(this::convertToDto);
        }
    }

    private BibliotecaDto convertToDto(Biblioteca biblioteca) {
        BibliotecaDto bibliotecaDto = new BibliotecaDto();
        bibliotecaDto.setId(biblioteca.getId());
        bibliotecaDto.setNombre(biblioteca.getNombre());
        bibliotecaDto.setDireccion(biblioteca.getDireccion());
        bibliotecaDto.setTelefono(biblioteca.getTelefono());
        bibliotecaDto.setEmail(biblioteca.getEmail());
        bibliotecaDto.setSitioWeb(biblioteca.getSitioWeb());
        return bibliotecaDto;
    }
}













