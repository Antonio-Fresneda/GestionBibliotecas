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
    @Data
    public class OrderCriteria {
        private String sortBy;
        private String valueSortOrder;
    }

    @Data
    public class SearchCriteria {
        private String key;
        private String operation;
        private String value;
    }

    @Data
    public class PageCriteria {
        private int pageIndex;
        private int pageSize;
    }

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
    @GetMapping("/filtros")
    public Page<Biblioteca> busquedaDinamica(
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
                            // Todos los parámetros están presentes
                            return bibliotecaRepository.findAllByNombreContainingAndDireccionContainingAndTelefonoContainingAndEmailContainingAndSitioWebContaining(nombre, direccion, telefono, email, sitioWeb, pageRequest);
                        } else {
                            // Sitio web no especificado
                            return bibliotecaRepository.findAllByNombreContainingAndDireccionContainingAndTelefonoContainingAndEmailContaining(nombre, direccion, telefono, email, pageRequest);
                        }
                    } else {
                        // Email no especificado
                        return bibliotecaRepository.findAllByNombreContainingAndDireccionContainingAndTelefonoContaining(nombre, direccion, telefono, pageRequest);
                    }
                } else {
                    // Teléfono no especificado
                    return bibliotecaRepository.findAllByNombreContainingAndDireccionContaining(nombre, direccion, pageRequest);
                }
            } else {
                // Dirección no especificada
                return bibliotecaRepository.findAllByNombreContaining(nombre, pageRequest);
            }
        } else {
            // Nombre no especificado
            return bibliotecaRepository.findAll(pageRequest);
        }
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

    @GetMapping("/libros")
    public Page<Libro> getLibros(
            @RequestParam(name = "titulo", required = false) String titulo,
            @RequestParam(name = "anoPublicacion", required = false) Integer anoPublicacion,
            @RequestParam(name = "isbn", required = false) String isbn,
            @RequestParam(name = "autorId", required = false) Long autorId,
            @RequestParam(name = "direccion", defaultValue = "asc") String direccion,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanoPagina", defaultValue = "10") int tamanoPagina) {

        Sort.Direction direction = Sort.Direction.ASC;
        if (direccion.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        PageRequest pageRequest = PageRequest.of(pagina, tamanoPagina, Sort.by(direction, "titulo"));

        if (titulo != null && anoPublicacion != null && isbn != null && autorId != null) {
            return libroRepository.findAllByTituloContainingAndAnoPublicacionAndIsbnContainingAndAutorId(titulo, anoPublicacion, isbn, autorId, pageRequest);
        } else if (titulo != null && anoPublicacion != null && isbn != null) {
            return libroRepository.findAllByTituloContainingAndAnoPublicacionAndIsbnContaining(titulo, anoPublicacion, isbn, pageRequest);
        } else if (titulo != null && anoPublicacion != null && autorId != null) {
            return libroRepository.findAllByTituloContainingAndAnoPublicacionAndAutorId(titulo, anoPublicacion, autorId, pageRequest);
        } else if (titulo != null && isbn != null && autorId != null) {
            return libroRepository.findAllByTituloContainingAndIsbnContainingAndAutorId(titulo, isbn, autorId, pageRequest);
        } else if (anoPublicacion != null && isbn != null && autorId != null) {
            return libroRepository.findAllByAnoPublicacionAndIsbnContainingAndAutorId(anoPublicacion, isbn, autorId, pageRequest);
        } else if (titulo != null && anoPublicacion != null) {
            return libroRepository.findAllByTituloContainingAndAnoPublicacion(titulo, anoPublicacion, pageRequest);
        } else if (titulo != null && isbn != null) {
            return libroRepository.findAllByTituloContainingAndIsbnContaining(titulo, isbn, pageRequest);
        } else if (titulo != null && autorId != null) {
            return libroRepository.findAllByTituloContainingAndAutorId(titulo, autorId, pageRequest);
        } else if (anoPublicacion != null && isbn != null) {
            return libroRepository.findAllByAnoPublicacionAndIsbnContaining(anoPublicacion, isbn, pageRequest);
        } else if (anoPublicacion != null && autorId != null) {
            return libroRepository.findAllByAnoPublicacionAndAutorId(anoPublicacion, autorId, pageRequest);
        } else if (isbn != null && autorId != null) {
            return libroRepository.findAllByIsbnContainingAndAutorId(isbn, autorId, pageRequest);
        } else if (titulo != null) {
            return libroRepository.findAllByTituloContaining(titulo, pageRequest);
        } else if (anoPublicacion != null) {
            return libroRepository.findAllByAnoPublicacion(anoPublicacion, pageRequest);
        } else if (isbn != null) {
            return libroRepository.findAllByIsbnContaining(isbn, pageRequest);
        } else if (autorId != null) {
            return libroRepository.findAllByAutorId(autorId, pageRequest);
        } else {
            return libroRepository.findAll(pageRequest);
        }
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

    @GetMapping("/generos")
    public Page<Genero> getGeneros(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "descripcion", required = false) String descripcion,
            @RequestParam(name = "edadRecomendada", required = false) Integer edadRecomendada,
            @RequestParam(name = "urlWikipedia", required = false) String urlWikipedia,
            @RequestParam(name = "direccion", defaultValue = "asc") String direccion,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanoPagina", defaultValue = "10") int tamanoPagina) {

        Sort.Direction direction = Sort.Direction.ASC;
        if (direccion.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        PageRequest pageRequest = PageRequest.of(pagina, tamanoPagina, Sort.by(direction, "nombre"));

        if (nombre != null && descripcion != null && edadRecomendada != null && urlWikipedia != null) {
            return generoRepository.findAllByNombreContainingAndDescripcionContainingAndEdadRecomendadaAndUrlWikipediaContaining(nombre, descripcion, edadRecomendada, urlWikipedia, pageRequest);
        } else if (nombre != null && descripcion != null && edadRecomendada != null) {
            return generoRepository.findAllByNombreContainingAndDescripcionContainingAndEdadRecomendada(nombre, descripcion, edadRecomendada, pageRequest);
        } else if (nombre != null && descripcion != null && urlWikipedia != null) {
            return generoRepository.findAllByNombreContainingAndDescripcionContainingAndUrlWikipediaContaining(nombre, descripcion, urlWikipedia, pageRequest);
        } else if (nombre != null && edadRecomendada != null && urlWikipedia != null) {
            return generoRepository.findAllByNombreContainingAndEdadRecomendadaAndUrlWikipediaContaining(nombre, edadRecomendada, urlWikipedia, pageRequest);
        } else if (descripcion != null && edadRecomendada != null && urlWikipedia != null) {
            return generoRepository.findAllByDescripcionContainingAndEdadRecomendadaAndUrlWikipediaContaining(descripcion, edadRecomendada, urlWikipedia, pageRequest);
        } else if (nombre != null && descripcion != null) {
            return generoRepository.findAllByNombreContainingAndDescripcionContaining(nombre, descripcion, pageRequest);
        } else if (nombre != null && edadRecomendada != null) {
            return generoRepository.findAllByNombreContainingAndEdadRecomendada(nombre, edadRecomendada, pageRequest);
        } else if (nombre != null && urlWikipedia != null) {
            return generoRepository.findAllByNombreContainingAndUrlWikipediaContaining(nombre, urlWikipedia, pageRequest);
        } else if (descripcion != null && edadRecomendada != null) {
            return generoRepository.findAllByDescripcionContainingAndEdadRecomendada(descripcion, edadRecomendada, pageRequest);
        } else if (descripcion != null && urlWikipedia != null) {
            return generoRepository.findAllByDescripcionContainingAndUrlWikipediaContaining(descripcion, urlWikipedia, pageRequest);
        } else if (edadRecomendada != null && urlWikipedia != null) {
            return generoRepository.findAllByEdadRecomendadaAndUrlWikipediaContaining(edadRecomendada, urlWikipedia, pageRequest);
        } else if (nombre != null) {
            return generoRepository.findAllByNombreContaining(nombre, pageRequest);
        } else if (descripcion != null) {
            return generoRepository.findAllByDescripcionContaining(descripcion, pageRequest);
        } else if (edadRecomendada != null) {
            return generoRepository.findAllByEdadRecomendada(edadRecomendada, pageRequest);
        } else if (urlWikipedia != null) {
            return generoRepository.findAllByUrlWikipediaContaining(urlWikipedia, pageRequest);
        } else {
            return generoRepository.findAll(pageRequest);
        }
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
        Page<Autor> autoresPage = autorRepository.findAllOrderedBy(ordenamientoRequest.getOrderBy(), direction.toString(), pageable);
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

    @GetMapping("/autores")
    public Page<Autor> getAutores(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "fechaNacimiento", required = false) Date fechaNacimiento,
            @RequestParam(name = "nacionalidad", required = false) String nacionalidad,
            @RequestParam(name = "direccion", defaultValue = "asc") String direccion,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanoPagina", defaultValue = "10") int tamanoPagina) {

        Sort.Direction direction = Sort.Direction.ASC;
        if (direccion.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        PageRequest pageRequest = PageRequest.of(pagina, tamanoPagina, Sort.by(direction, "nombre"));


        if (nombre != null && !nombre.isEmpty() && fechaNacimiento != null && nacionalidad != null && !nacionalidad.isEmpty()) {
            return autorRepository.findAllByNombreAndFechaNacimientoAndNacionalidad(nombre, fechaNacimiento, nacionalidad, pageRequest);
        } else if (nombre != null && !nombre.isEmpty() && fechaNacimiento != null) {
            return autorRepository.findAllByNombreAndFechaNacimiento(nombre, fechaNacimiento, pageRequest);
        } else if (nombre != null && !nombre.isEmpty() && nacionalidad != null && !nacionalidad.isEmpty()) {
            return autorRepository.findAllByNombreAndNacionalidad(nombre, nacionalidad, pageRequest);
        } else if (fechaNacimiento != null && nacionalidad != null && !nacionalidad.isEmpty()) {
            return autorRepository.findAllByFechaNacimientoAndNacionalidad(fechaNacimiento, nacionalidad, pageRequest);
        } else if (nombre != null && !nombre.isEmpty()) {
            return autorRepository.findAllByNombre(nombre, pageRequest);
        } else if (fechaNacimiento != null) {
            return autorRepository.findAllByFechaNacimiento(fechaNacimiento, pageRequest);
        } else if (nacionalidad != null && !nacionalidad.isEmpty()) {
            return autorRepository.findAllByNacionalidad(nacionalidad, pageRequest);
        } else {
            return autorRepository.findAll(pageRequest);
        }
    }

    @Data
    public class AutorSearchRequest {
        private List<BibliotecaRestController.OrderCriteria> listOrderCriteria;
        private List<BibliotecaRestController.SearchCriteria> listSearchCriteria;
        private BibliotecaRestController.PageCriteria page;
    }

    /*@PostMapping("/autores")
    public Page<Autor> getAutores(@RequestBody AutorSearchRequest searchRequest) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (searchRequest.getListOrderCriteria() != null && !searchRequest.getListOrderCriteria().isEmpty()) {
            BibliotecaRestController.OrderCriteria orderCriteria = searchRequest.getListOrderCriteria().get(0);
            if (orderCriteria.getValueSortOrder().equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
        }

        PageRequest pageRequest = PageRequest.of(searchRequest.getPage().getPageIndex(), searchRequest.getPage().getPageSize(), Sort.by(direction, "nombre"));

        List<BibliotecaRestController.SearchCriteria> listSearchCriteria = searchRequest.getListSearchCriteria();
        if (listSearchCriteria != null && !listSearchCriteria.isEmpty()) {
            // Construir la especificación de búsqueda dinámicamente
            Specification<Autor> spec = Specification.where(null);
            for (BibliotecaRestController.SearchCriteria criteria : listSearchCriteria) {
                if (criteria.getKey().equals("nombre")) {
                    spec = spec.and((root, query, cb) -> cb.equal(root.get("nombre"), criteria.getValue()));
                } else if (criteria.getKey().equals("fechaNacimiento")) {
                    spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaNacimiento"), criteria.getValue()));
                } else if (criteria.getKey().equals("nacionalidad")) {
                    spec = spec.and((root, query, cb) -> cb.equal(root.get("nacionalidad"), criteria.getValue()));
                }
            }

            return autorRepository.findAll(spec, pageRequest);
        } else {
            return autorRepository.findAll(pageRequest);
        }
    }

     */
}





