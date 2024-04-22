package com.gestion.controller;

import com.gestion.dto.AutorDto;
import com.gestion.dto.LibroDto;
import com.gestion.entities.*;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.AutorRepository;
import com.gestion.repository.BibliotecaRepository;
import com.gestion.repository.GeneroRepository;
import com.gestion.repository.LibroRepository;
import com.gestion.search.BusquedaLibroRequest;
import com.gestion.search.OrderCriteria;
import com.gestion.search.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/libro")
public class LibroRestController {

    @Autowired
    LibroRepository libroRepository;

    @Autowired
    GeneroRepository generoRepository;

    @Autowired
    AutorRepository autorRepository;

    @Autowired
    BibliotecaRepository bibliotecaRepository;

    @GetMapping()
    public List<LibroDto> list() {
        List<Libro> libros = libroRepository.findAll();
        List<LibroDto> libroDTOs = new ArrayList<>();
        for (Libro libro : libros) {
            libroDTOs.add(convertToDto(libro));
        }
        return libroDTOs;
    }

    @GetMapping("/{id}")
    public LibroDto get(@PathVariable(name = "id") long id) {
        Libro libro = libroRepository.findById(id).orElseThrow(() -> new BibliotecaNotFoundException("Libro not found with id: " + id));
        return convertToDto(libro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Libro input) {
        Libro find = libroRepository.findById(id).orElseThrow(() -> new BibliotecaNotFoundException("Libro not found with id: " + id));
        if (find != null) {
            find.setTitulo(input.getTitulo());
            find.setAnoPublicacion(input.getAnoPublicacion());
            find.setIsbn(input.getIsbn());
            find.setAutor(input.getAutor());
            find.setGenero(input.getGenero());

        }
        Libro save = libroRepository.save(find);
        return ResponseEntity.ok(save);
    }
    /*@PostMapping("/crear")
    public ResponseEntity<Libro> crearLibro(@RequestBody Libro libro) {

        Autor autor = libro.getAutor();
        if (autor != null && (autor.getId() == null || autor.getId() == 0)) {
            autor = autorRepository.save(autor);
        }

        for (LibroGenero libroGenero : libro.getGeneros()) {
            Genero genero = libroGenero.getGenero();
            if (genero != null && (genero.getId() == null || genero.getId() == 0)) {
                // Si el género no tiene un ID válido, lo guardamos como un nuevo género
                genero = generoRepository.save(genero);
            } else if (genero != null) {
                // Si el género tiene un ID válido, verificamos si existe en la base de datos
                Optional<Genero> existingGenero = generoRepository.findById(genero.getId());
                if (existingGenero.isEmpty()) {
                    // Si no existe, lo guardamos como un nuevo género
                    genero = generoRepository.save(genero);
                }
            }
        }

        Libro nuevoLibro = libroRepository.save(libro);

        return new ResponseEntity<>(nuevoLibro, HttpStatus.CREATED);
    }
     */
    @PostMapping()
    public ResponseEntity<Libro> crearLibro(@RequestBody Libro libro) {

        Autor autor = libro.getAutor();
        if (autor != null && autor.getNombre() != null && autor.getFechaNacimiento() != null && autor.getNacionalidad() != null) {
            // Comprobar si el autor ya existe en la base de datos por nombre, fecha de nacimiento y nacionalidad
            List<Autor> existingAutorList = autorRepository.findByNombreAndFechaNacimientoAndNacionalidad(autor.getNombre(), autor.getFechaNacimiento(), autor.getNacionalidad());
            if (!existingAutorList.isEmpty()) {
                // Si el autor ya existe, asignamos el existente en lugar de crear uno nuevo
                libro.setAutor(existingAutorList.get(0)); // Suponiendo que no debería haber duplicados
            } else {
                // Si no existe, lo guardamos como un nuevo autor
                autor = autorRepository.save(autor);
                libro.setAutor(autor);
            }
        }
        Genero genero = libro.getGenero();
        if (genero != null && genero.getNombre() != null && !genero.getNombre().isEmpty()) {
            // Buscar el género por nombre en la base de datos
            Genero existingGenero = (Genero) generoRepository.findByNombre(genero.getNombre());

            if (existingGenero != null) {
                // Si el género ya existe en la base de datos, lo asignamos al libro
                libro.setGenero(existingGenero);
            } else {
                // Si el género no existe en la base de datos, lo guardamos y luego lo asignamos al libro
                genero = generoRepository.save(genero);
                libro.setGenero(genero);
            }
        }



        Libro nuevoLibro = libroRepository.save(libro);

        return new ResponseEntity<>(nuevoLibro, HttpStatus.CREATED);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLibro(@PathVariable(name = "id") long id) {
        Optional<Libro> libroOptional = libroRepository.findById(id);
        if (libroOptional.isEmpty()) {
            throw new BibliotecaNotFoundException("Libro with id " + id + " not found");
        }

        libroRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/libros")
    public List<LibroDto> getLibros(
            @RequestParam(name = "titulo", required = false) String titulo,
            @RequestParam(name = "anoPublicacion", required = false) Integer anoPublicacion,
            @RequestParam(name = "isbn", required = false) String isbn,
            @RequestParam(name = "direccion", defaultValue = "asc") String direccion,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanoPagina", defaultValue = "10") int tamanoPagina) {

        Sort.Direction direction = Sort.Direction.ASC;
        if (direccion.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        PageRequest pageRequest = PageRequest.of(pagina, tamanoPagina, Sort.by(direction, "titulo"));

        Page<Libro> librosPage;

        if (titulo != null && anoPublicacion != null && isbn != null) {
            librosPage = libroRepository.findAllByTituloContainingAndAnoPublicacionAndIsbnContaining(titulo, anoPublicacion, isbn, pageRequest);
        } else if (titulo != null && anoPublicacion != null) {
            librosPage = libroRepository.findAllByTituloContainingAndAnoPublicacion(titulo, anoPublicacion, pageRequest);
        } else if (titulo != null && isbn != null) {
            librosPage = libroRepository.findAllByTituloContainingAndIsbnContaining(titulo, isbn, pageRequest);
        } else if (anoPublicacion != null && isbn != null) {
            librosPage = libroRepository.findAllByAnoPublicacionAndIsbnContaining(anoPublicacion, isbn, pageRequest);
        } else if (titulo != null) {
            librosPage = libroRepository.findAllByTituloContaining(titulo, pageRequest);
        } else if (anoPublicacion != null) {
            librosPage = libroRepository.findAllByAnoPublicacion(anoPublicacion, pageRequest);
        } else if (isbn != null) {
            librosPage = libroRepository.findAllByIsbnContaining(isbn, pageRequest);
        } else {
            librosPage = libroRepository.findAll(pageRequest);
        }

        List<LibroDto> libroDtoList = librosPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return libroDtoList;
    }





    @Autowired
    private EntityManager entityManager;

    /*@PostMapping("/buscar-libros")
    public List<LibroDto> buscarLibros(@RequestBody BusquedaLibroRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Libro> criteriaQuery = criteriaBuilder.createQuery(Libro.class);
        Root<Libro> root = criteriaQuery.from(Libro.class);

        // Aplicar criterios de búsqueda
        Predicate predicate = criteriaBuilder.conjunction();
        for (SearchCriteria criteria : request.getListSearchCriteria()) {
            predicate = criteriaBuilder.and(predicate, getPredicate(criteria, criteriaBuilder, root));
        }
        criteriaQuery.where(predicate);

        // Ordenar según criterios de orden
        for (OrderCriteria orderCriteria : request.getListOrderCriteria()) {
            if (orderCriteria.getSortBy() != null && !orderCriteria.getSortBy().isEmpty()) {
                if (orderCriteria.getValueSortOrder() != null && !orderCriteria.getValueSortOrder().isEmpty()) {
                    if (orderCriteria.getValueSortOrder().equalsIgnoreCase("ASC")) {
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(orderCriteria.getSortBy())));
                    } else if (orderCriteria.getValueSortOrder().equalsIgnoreCase("DESC")) {
                        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(orderCriteria.getSortBy())));
                    }
                }
            }
        }

        // Aplicar paginación
        List<Libro> libros = entityManager.createQuery(criteriaQuery)
                .setFirstResult(request.getPage().getPageIndex() * request.getPage().getPageSize())
                .setMaxResults(request.getPage().getPageSize())
                .getResultList();

        // Convertir los libros a LibroDto
        List<LibroDto> librosDto = new ArrayList<>();
        for (Libro libro : libros) {
            librosDto.add(convertirALibroDto(libro));
        }

        return librosDto;
    }

     */
    @PostMapping("/buscar-libros")
    public List<LibroDto> buscarLibros(@RequestBody BusquedaLibroRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Libro> criteriaQuery = criteriaBuilder.createQuery(Libro.class);
        Root<Libro> root = criteriaQuery.from(Libro.class);

        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : request.getListSearchCriteria()) {
            String key = criteria.getKey();
            String value = criteria.getValue().toString(); // Convertimos el valor a String

            switch (key) {
                case "titulo":
                case "isbn":
                    predicates.add(criteriaBuilder.like(root.get(key), "%" + value + "%"));
                    break;
                case "anoPublicacion":
                    try {
                        int ano = Integer.parseInt(value);
                        predicates.add(criteriaBuilder.equal(root.get(key), ano));
                    } catch (NumberFormatException e) {
                        // Ignorar si no se puede convertir a entero
                    }
                    break;
                case "autorId":
                case "generoId":
                    try {
                        Long id = Long.parseLong(value);
                        predicates.add(criteriaBuilder.equal(root.get(key), id));
                    } catch (NumberFormatException e) {
                        // Ignorar si no se puede convertir a Long
                    }
                    break;
                default:
                    // Ignorar cualquier otro atributo
                    break;
            }
        }

        if (!predicates.isEmpty()) {
            Predicate finalPredicate = criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            criteriaQuery.where(finalPredicate);
        }

        for (OrderCriteria orderCriteria : request.getListOrderCriteria()) {
            if (orderCriteria.getSortBy() != null && !orderCriteria.getSortBy().isEmpty()) {
                if (orderCriteria.getValueSortOrder() != null && !orderCriteria.getValueSortOrder().isEmpty()) {
                    if (orderCriteria.getValueSortOrder().equalsIgnoreCase("ASC")) {
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(orderCriteria.getSortBy())));
                    } else if (orderCriteria.getValueSortOrder().equalsIgnoreCase("DESC")) {
                        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(orderCriteria.getSortBy())));
                    }
                }
            }
        }

        List<Libro> libros = entityManager.createQuery(criteriaQuery)
                .setFirstResult(request.getPage().getPageIndex() * request.getPage().getPageSize())
                .setMaxResults(request.getPage().getPageSize())
                .getResultList();

        List<LibroDto> librosDto = new ArrayList<>();
        for (Libro libro : libros) {
            librosDto.add(convertToDto(libro));
        }

        return librosDto;
    }


    private Predicate getPredicate(SearchCriteria criteria, CriteriaBuilder builder, Root<Libro> root) {
        switch (criteria.getOperation()) {
            case "EQUALS":
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case "GREATER_THAN":
                return builder.greaterThan(root.get(criteria.getKey()), criteria.getValue());
            case "LESS_THAN":
                return builder.lessThan(root.get(criteria.getKey()), criteria.getValue());
            case "CONTAINS":
                if (criteria.getValue() instanceof String) {
                    String value = (String) criteria.getValue();
                    return builder.like(root.get(criteria.getKey()), "%" + value + "%");
                }
                break;
            default:
                return null;
        }
        return null;
    }


    private LibroDto convertToDto(Libro libro) {
        LibroDto libroDTO = new LibroDto();
        libroDTO.setId(libro.getId());
        libroDTO.setTitulo(libro.getTitulo());
        libroDTO.setAnoPublicacion(libro.getAnoPublicacion());
        libroDTO.setIsbn(libro.getIsbn());
        libroDTO.setAutorId(libro.getAutor().getId());
        libroDTO.setGeneroId(libro.getGenero().getId());


        /*List<Long> generoIds = new ArrayList<>();
        for (LibroGenero libroGenero : libro.getGeneros()) {
            generoIds.add(libroGenero.getGenero().getId());
        }
        libroDTO.setGeneroIds(generoIds);

         */

        return libroDTO;
    }
}
