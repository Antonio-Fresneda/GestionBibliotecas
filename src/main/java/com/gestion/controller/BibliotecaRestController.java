package com.gestion.controller;


import com.gestion.entities.Autor;
import com.gestion.entities.Biblioteca;
import com.gestion.entities.Genero;
import com.gestion.entities.Libro;
import com.gestion.repository.AutorRepository;
import com.gestion.repository.BibliotecaRepository;
import com.gestion.repository.GeneroRepository;
import com.gestion.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @GetMapping("/por-nombre")
    public List<Biblioteca> getBibliotecaPorNombres(@RequestParam(name = "nombre") String nombre) {
        return bibliotecaRepository.findAllByNombre(nombre);
    }

    @GetMapping("/por-direccion")
    public List<Biblioteca> getBibliotecaPorDireccion(@RequestParam(name = "direccion") String direccion) {
        return bibliotecaRepository.findAllByDireccion(direccion);
    }

    @GetMapping("/por-telefono")
    public List<Biblioteca> getBibliotecaPorTelefono(@RequestParam(name = "telefono") String telefono) {
        return bibliotecaRepository.findAllByTelefono(telefono);
    }

    @GetMapping("/por-email")
    public List<Biblioteca> getBibliotecaPorEmail(@RequestParam(name = "email") String email) {
        return bibliotecaRepository.findAllByEmail(email);
    }
    @GetMapping("/por-sitioWeb")
    public List<Biblioteca> getBibliotecaPorWeb(@RequestParam(name = "sitioWeb") String sitioWeb) {
        return bibliotecaRepository.findAllByWeb(sitioWeb);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Biblioteca input) {
        Biblioteca find = bibliotecaRepository.findById(id).get();
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
    @RestController
    @RequestMapping("/libro")
    class LibroRestController {

        @Autowired
        LibroRepository libroRepository;

        @GetMapping()
        public List<Libro> list() {
            return libroRepository.findAll();
        }

        @GetMapping("/{id}")
        public Libro get(@PathVariable(name = "id") long id) {
            return libroRepository.findById(id).get();
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Libro input) {
            Libro find = libroRepository.findById(id).get();
            if (find != null) {
                find.setTitulo(input.getTitulo());
                find.setAnoPublicacion(input.getAnoPublicacion());
                find.setIsbn(input.getIsbn());
            }
            Libro save = libroRepository.save(find);
            return ResponseEntity.ok(save);
        }
        @GetMapping("/por-titulo")
        public List<Libro> getLibrosPorTitulo(@RequestParam(name = "titulo") String titulo) {
            return libroRepository.findAllByTitulo(titulo);
        }

        @GetMapping("/por-ano-publicacion")
        public List<Libro> getLibrosPorAnoPublicacion(@RequestParam(name = "anoPublicacion") int anoPublicacion) {
            return libroRepository.findAllByAnoPublicacion(anoPublicacion);
        }

        @GetMapping("/por-isbn")
        public List<Libro> getLibrosPorISBN(@RequestParam(name = "isbn") String isbn) {
            return libroRepository.findAllByIsbn(isbn);
        }

        @GetMapping("/por-autor")
        public List<Libro> getLibrosPorAutor(@RequestParam(name = "autor") Autor autor) {
            return libroRepository.findAllByAutor(autor);
        }

        @PostMapping
        public ResponseEntity<?> post(@RequestBody Libro input) {
            input.getGeneros().forEach(x -> x.setLibro(input));
            Libro save = libroRepository.save(input);
            return ResponseEntity.ok(save);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
            Optional<Libro> findById = libroRepository.findById(id);
            if (findById.get() != null) {
                libroRepository.delete(findById.get());
            }
            return ResponseEntity.ok().build();
        }
    }
@RestController
@RequestMapping("/genero")
class GeneroRestController {

    @Autowired
    GeneroRepository generoRepository;

    @GetMapping()
    public List<Genero> list() {
        return generoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Genero get(@PathVariable(name = "id") long id) {
        return generoRepository.findById(id).orElse(null);
    }

    @GetMapping("/por-nombre")
    public List<Genero> getGenerosPorNombre(@RequestParam(name = "nombre") String nombre) {
        return generoRepository.findAllByNombre(nombre);
    }

    @GetMapping("/por-descripcion")
    public List<Genero> getGenerosPorDescripcion(@RequestParam(name = "descripcion") String descripcion) {
        return generoRepository.findAllByDescripcion(descripcion);
    }

    @GetMapping("/por-edad-recomendada")
    public List<Genero> getGenerosPorEdadRecomendada(@RequestParam(name = "edadRecomendada") String edadRecomendada) {
        return generoRepository.findAllByEdadRecomendada(edadRecomendada);
    }

    @GetMapping("/por-url-wikipedia")
    public List<Genero> getGenerosPorUrlWikipedia(@RequestParam(name = "urlWikipedia") String urlWikipedia) {
        return generoRepository.findAllByUrlWikipedia(urlWikipedia);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Genero input) {
        Genero find = generoRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNombre(input.getNombre());
            find.setDescripcion(input.getDescripcion());
            find.setEdadRecomendada(input.getEdadRecomendada());
            find.setUrlWikipedia(input.getUrlWikipedia());
        }
        Genero save = generoRepository.save(find);
        return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Genero input) {
        Genero save = generoRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Genero> findById = generoRepository.findById(id);
        if (findById.isPresent()) {
            generoRepository.delete(findById.get());
        }
        return ResponseEntity.ok().build();
    }
}

@RestController
@RequestMapping("/autor")
class AutorRestController {

    @Autowired
    AutorRepository autorRepository;

    @GetMapping()
    public List<Autor> list() {
        return autorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Autor get(@PathVariable(name = "id") long id) {
        return autorRepository.findById(id).orElse(null);
    }

    @GetMapping("/autores-por-nombre")
    public List<Autor> getAutoresPorNombre(@RequestParam(name = "nombre") String nombre) {
        return autorRepository.findAllByNombre(nombre);
    }

    @GetMapping("/autores-por-fecha-nacimiento")
    public List<Autor> getAutoresPorFechaNacimiento(@RequestParam(name = "fechaNacimiento") Date fechaNacimiento) {
        return autorRepository.findAllByFechaNacimiento(fechaNacimiento);
    }

    @GetMapping("/autores-por-nacionalidad")
    public List<Autor> getAutoresPorNacionalidad(@RequestParam(name = "nacionalidad") String nacionalidad) {
        return autorRepository.findAllByNacionalidad(nacionalidad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Autor input) {
        Autor find = autorRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNombre(input.getNombre());
            find.setFechaNacimiento(input.getFechaNacimiento());
            find.setNacionalidad(input.getNacionalidad());
        }
        Autor save = autorRepository.save(find);
        return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Autor input) {
        Autor save = autorRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Autor> findById = autorRepository.findById(id);
        if (findById.isPresent()) {
            autorRepository.delete(findById.get());
        }
        return ResponseEntity.ok().build();
    }
}


