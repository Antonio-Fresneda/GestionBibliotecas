package com.gestion.controller;


import com.gestion.entities.*;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.AutorRepository;
import com.gestion.repository.BibliotecaRepository;
import com.gestion.repository.GeneroRepository;
import com.gestion.repository.LibroRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    public List<Biblioteca> getAll() {
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

    /*@GetMapping("/Ordenar")
    public ResponseEntity<List<Biblioteca>> getOrderBiblioteca(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "nombre") String orderBy) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Biblioteca> bibliotecaPage = bibliotecaRepository.findAllBibliotecaOrderedBy(orderBy, pageable);

        List<Biblioteca> bibliotecaList = bibliotecaPage.getContent();

        return new ResponseEntity<>(bibliotecaList, HttpStatus.OK);
    }
     */
    @PostMapping("/ordenar")
    public ResponseEntity<Page<Biblioteca>> getAllGeneros(@RequestBody AutorRestController.OrdenamientoRequest ordenamientoRequest) {
        Sort.Direction direction = Sort.Direction.fromString(ordenamientoRequest.getOrderDirection().toUpperCase());
        Pageable pageable = PageRequest.of(ordenamientoRequest.getPage(), ordenamientoRequest.getSize(), direction, ordenamientoRequest.getOrderBy());
        Page<Biblioteca> autoresPage = bibliotecaRepository.findAllOrderedBy(ordenamientoRequest.getOrderBy(), pageable);
        return new ResponseEntity<>(autoresPage, HttpStatus.OK);
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
            find.setLibroBibliotecas(input.getLibroBibliotecas());
            } else {
            throw new BibliotecaNotFoundException("Biblioteca not found with id: " + id);
        }
        Biblioteca save = bibliotecaRepository.save(find);
        return ResponseEntity.ok(save);
    }


    @PostMapping
    public ResponseEntity<?> post(@RequestBody Biblioteca input) {

        BibliotecaLibro bibliotecaLibro = new BibliotecaLibro();
        bibliotecaLibro.setBiblioteca(input);

        input.getLibroBibliotecas().add(bibliotecaLibro);

        Biblioteca savedBiblioteca = bibliotecaRepository.save(input);

        return ResponseEntity.ok(savedBiblioteca);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Biblioteca> findById = bibliotecaRepository.findById(id);
        if (findById.isPresent()) {
            bibliotecaRepository.delete(findById.get());
        } else {
            throw new BibliotecaNotFoundException("Biblioteca not found with id: " + id);
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
        return libroRepository.findById(id).orElseThrow(() -> new BibliotecaNotFoundException("Libro not found with id: " + id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Libro input) {
        Libro find = libroRepository.findById(id).orElseThrow(() -> new BibliotecaNotFoundException("Libro not found with id: " + id));
        if (find != null) {
            find.setTitulo(input.getTitulo());
            find.setAnoPublicacion(input.getAnoPublicacion());
            find.setIsbn(input.getIsbn());
            find.setAutor(input.getAutor());

            if (input.getGeneros() != null) {
                find.getGeneros().clear();
                find.getGeneros().addAll(input.getGeneros());
                for (LibroGenero genero : find.getGeneros()) {
                    genero.setLibro(find);
                }
            }

            find.setLibroBibliotecas(input.getLibroBibliotecas());
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

   /* @GetMapping("/por-autor")
    public List<Libro> getLibrosPorAutor(@RequestParam(name = "autor") Autor autor) {
        return libroRepository.findAllByAutor(autor);
    }

   /* @GetMapping("/Ordenar")
    public ResponseEntity<List<Libro>> getOrderLibro(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "nombre") String orderBy, @RequestParam(defaultValue = "asc") String orderDirection) {

        Sort.Direction direction = Sort.Direction.fromString(orderDirection.toLowerCase());
        Pageable pageable = PageRequest.of(page, size);
        Page<Libro> libroPage = libroRepository.findAllLibroOrderedBy(orderBy, pageable);

        List<Libro> libroList = libroPage.getContent();

        return new ResponseEntity<>(libroList, HttpStatus.OK);
    }

    */
   @PostMapping("/ordenar")
   public ResponseEntity<Page<Libro>> getAllGeneros(@RequestBody AutorRestController.OrdenamientoRequest ordenamientoRequest) {
       Sort.Direction direction = Sort.Direction.fromString(ordenamientoRequest.getOrderDirection().toUpperCase());
       Pageable pageable = PageRequest.of(ordenamientoRequest.getPage(), ordenamientoRequest.getSize(), direction, ordenamientoRequest.getOrderBy());
       Page<Libro> autoresPage = libroRepository.findAllOrderedBy(ordenamientoRequest.getOrderBy(), pageable);
       return new ResponseEntity<>(autoresPage, HttpStatus.OK);
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
        if (findById.isPresent()) {
            libroRepository.delete(findById.get());
        } else {
            throw new BibliotecaNotFoundException("Libro not found with id: " + id);
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

    @PostMapping("/ordenar")
    public ResponseEntity<Page<Genero>> getAllGeneros(@RequestBody AutorRestController.OrdenamientoRequest ordenamientoRequest) {
        Sort.Direction direction = Sort.Direction.fromString(ordenamientoRequest.getOrderDirection().toUpperCase());
        Pageable pageable = PageRequest.of(ordenamientoRequest.getPage(), ordenamientoRequest.getSize(), direction, ordenamientoRequest.getOrderBy());
        Page<Genero> autoresPage = generoRepository.findAllOrderedBy(ordenamientoRequest.getOrderBy(), pageable);
        return new ResponseEntity<>(autoresPage, HttpStatus.OK);
    }


   /* @PostMapping("/ordenar")
    public ResponseEntity<Page<Genero>> getAllGeneros(@RequestBody AutorRestController.OrdenamientoRequest ordenamientoRequest) {
        Sort.Direction direction = Sort.Direction.fromString(ordenamientoRequest.getOrderDirection().toUpperCase());
        Pageable pageable = PageRequest.of(ordenamientoRequest.getPage(), ordenamientoRequest.getSize(), direction, ordenamientoRequest.getOrderBy());
        Page<Genero> autoresPage = generoRepository.findAllOrderedBy(ordenamientoRequest.getOrderBy(), ordenamientoRequest.getOrderDirection(), pageable);
        return new ResponseEntity<>(autoresPage, HttpStatus.OK);
    }

    */




    /*@GetMapping("/Ordenar")
    public ResponseEntity<Page<Autor>> getAllGenero(
            @RequestParam("orderBy") String orderBy,
            @RequestParam("orderDirection") String orderDirection,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        Sort.Direction direction = Sort.Direction.fromString(orderDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        System.out.println("OrderDirection: " + orderDirection);

        Page<Autor> autoresPage = generoRepository.findAllGeneroOrderedBy(orderBy, orderDirection, pageable);


        return new ResponseEntity<>(autoresPage, HttpStatus.OK);
    }

     */

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Genero input) {
        Genero find = generoRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNombre(input.getNombre());
            find.setDescripcion(input.getDescripcion());
            find.setEdadRecomendada(input.getEdadRecomendada());
            find.setUrlWikipedia(input.getUrlWikipedia());
        } else {
            throw new BibliotecaNotFoundException("Genero not found with id: " + id);
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
        } else {
            throw new BibliotecaNotFoundException("Genero not found with id: " + id);
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
        return autorRepository.findById(id).orElseThrow(() -> new BibliotecaNotFoundException("Autor not found with id: " + id));
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


    /*@GetMapping("/Ordenar")
    public ResponseEntity<Page<Autor>> getAllAutores(
            @RequestParam("orderBy") String orderBy,
            @RequestParam("orderDirection") String orderDirection,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        Sort.Direction direction = Sort.Direction.fromString(orderDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        System.out.println("OrderDirection: " + orderDirection);

        Page<Autor> autoresPage = autorRepository.findAllOrderedBy(orderBy, orderDirection, pageable);


        return new ResponseEntity<>(autoresPage, HttpStatus.OK);
    }
     */
    @Data
    public static class OrdenamientoRequest {
        private String orderBy;
        private String orderDirection;
        private int page;
        private int size;

        public OrdenamientoRequest() {

        }
    }

    @PostMapping("/ordenar")
    public ResponseEntity<Page<Autor>> getAllAutores(@RequestBody OrdenamientoRequest ordenamientoRequest) {
        Sort.Direction direction = Sort.Direction.fromString(ordenamientoRequest.getOrderDirection().toUpperCase());
        Pageable pageable = PageRequest.of(ordenamientoRequest.getPage(), ordenamientoRequest.getSize(), direction, ordenamientoRequest.getOrderBy());
        Page<Autor> autoresPage = autorRepository.findAllOrderedBy(ordenamientoRequest.getOrderBy(), pageable);
        return new ResponseEntity<>(autoresPage, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Autor input) {
        Autor find = autorRepository.findById(id).orElseThrow(() -> new BibliotecaNotFoundException("Autor not found with id: " + id));
        find.setNombre(input.getNombre());
        find.setFechaNacimiento(input.getFechaNacimiento());
        find.setNacionalidad(input.getNacionalidad());
        Autor save = autorRepository.save(find);
        return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Autor input) {
        Autor save = autorRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Autor> autorOptional = autorRepository.findById(id);
        if (autorOptional.isPresent()) {
            Autor autor = autorOptional.get();
            autorRepository.deleteLibroGeneroByAutorId(autor.getId());
            autorRepository.deleteLibrosByAutorId(autor.getId());
            autorRepository.delete(autor);
            return ResponseEntity.ok().build();
        } else {
            throw new BibliotecaNotFoundException("Autor not found with id: " + id);
        }
    }

    /*@GetMapping("/Ordenar")
    public ResponseEntity<List<Autor>> getAllAutores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String orderBy) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Autor> autoresPage = autorRepository.findAllOrderedBy(orderBy, pageable);

        List<Autor> autoresList = autoresPage.getContent();

        return new ResponseEntity<>(autoresList, HttpStatus.OK);
    }
     */
    /*@GetMapping("/Ordenar")
    public ResponseEntity<List<Autor>> getAllAutores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String orderBy,
            @RequestParam(defaultValue = "asc") String orderDirection) {

        Sort.Direction direction = Sort.Direction.fromString(orderDirection.toLowerCase());
        Pageable pageable = PageRequest.of(page, size, direction, orderBy);

        Page<Autor> autoresPage = autorRepository.findAllOrderedBy(orderBy, pageable);

        List<Autor> autoresList = autoresPage.getContent();

        return new ResponseEntity<>(autoresList, HttpStatus.OK);
    }

     */

}


