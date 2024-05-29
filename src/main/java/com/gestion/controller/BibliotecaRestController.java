package com.gestion.controller;


import com.gestion.dto.BibliotecaDto;
import com.gestion.dto.LibroDto;
import com.gestion.entities.*;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
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


    @PreAuthorize("hasAnyAuthority('ESCRIBIR_BIBLIOTECA','LEER_BIBLIOTECA')")
    @GetMapping()
    public List<BibliotecaDto> getAll() {
        List<Biblioteca> bibliotecas = bibliotecaRepository.findAll();
        return bibliotecas.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = bibliotecaRepository.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}")
    public BibliotecaDto get(@PathVariable(name = "id") long id) {
        Biblioteca biblioteca = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new BibliotecaNotFoundException("Biblioteca not found with id: " + id));
        return convertToDto(biblioteca);
    }

    @PreAuthorize("hasAuthority('ESCRIBIR_BIBLIOTECA')")
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Biblioteca input) {
        Biblioteca biblioteca = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new BibliotecaNotFoundException("Biblioteca not found with id: " + id));
        biblioteca.setNombre(input.getNombre());
        biblioteca.setDireccion(input.getDireccion());
        biblioteca.setTelefono(input.getTelefono());
        biblioteca.setEmail(input.getEmail());
        biblioteca.setSitioWeb(input.getSitioWeb());
        biblioteca.setLibros(input.getLibros());
        Biblioteca savedBiblioteca = bibliotecaRepository.save(biblioteca);
        return ResponseEntity.ok(convertToDto(savedBiblioteca));
    }

    @PreAuthorize("hasAuthority('ESCRIBIR_BIBLIOTECA')")
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Biblioteca input) {
        Biblioteca save = bibliotecaRepository.save(input);
        return ResponseEntity.ok((save));
    }

    @PreAuthorize("hasAuthority('ESCRIBIR_BIBLIOTECA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBiblioteca(@PathVariable(name = "id") long id) {
        Optional<Biblioteca> bibliotecaOptional = bibliotecaRepository.findById(id);
        if (bibliotecaOptional.isEmpty()) {
            throw new BibliotecaNotFoundException("Biblioteca with id " + id + " not found");
        }

        Biblioteca biblioteca = bibliotecaOptional.get();
        biblioteca.getLibros().clear();
        bibliotecaRepository.save(biblioteca);
        bibliotecaRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasAnyAuthority('ESCRIBIR_BIBLIOTECA','LEER_BIBLIOTECA')")
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


    @Autowired
    private EntityManager entityManager;

    @PreAuthorize("hasAnyAuthority('ESCRIBIR_BIBLIOTECA','LEER_BIBLIOTECA')")
    @PostMapping("/buscar-bibliotecas")
    public List<BibliotecaDto> buscarBibliotecas(@RequestBody BusquedaLibroRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Biblioteca> criteriaQuery = criteriaBuilder.createQuery(Biblioteca.class);
        Root<Biblioteca> root = criteriaQuery.from(Biblioteca.class);

        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : request.getListSearchCriteria()) {
            String key = criteria.getKey();
            String value = criteria.getValue().toString(); // Convertimos el valor a String

            switch (key) {
                case "nombre":
                case "direccion":
                case "email":
                case "sitioWeb":
                    predicates.add(criteriaBuilder.like(root.get(key), "%" + value + "%"));
                    break;
                case "telefono":
                    predicates.add(criteriaBuilder.like(root.get(key), "%" + value + "%"));
                    break;
                default:
                    // Manejar casos adicionales según sea necesario
            }
        }

        Predicate finalPredicate = criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        criteriaQuery.where(finalPredicate);

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

        List<Biblioteca> bibliotecas = entityManager.createQuery(criteriaQuery)
                .setFirstResult(request.getPage().getPageIndex() * request.getPage().getPageSize())
                .setMaxResults(request.getPage().getPageSize())
                .getResultList();

        List<BibliotecaDto> bibliotecasDto = new ArrayList<>();
        for (Biblioteca biblioteca : bibliotecas) {
            bibliotecasDto.add(convertToDto(biblioteca));
        }

        return bibliotecasDto;
    }


    private Predicate getPredicate(SearchCriteria criteria, CriteriaBuilder builder, Root<Biblioteca> root) {
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

    private BibliotecaDto convertToDto(Biblioteca biblioteca) {
        BibliotecaDto bibliotecaDto = new BibliotecaDto();
        bibliotecaDto.setId(biblioteca.getId());
        bibliotecaDto.setNombre(biblioteca.getNombre());
        bibliotecaDto.setDireccion(biblioteca.getDireccion());
        bibliotecaDto.setTelefono(biblioteca.getTelefono());
        bibliotecaDto.setEmail(biblioteca.getEmail());
        bibliotecaDto.setSitioWeb(biblioteca.getSitioWeb());


        Set<Libro> libros = biblioteca.getLibros();

        List<String> titulosLibros = new ArrayList<>();

        for (Libro libro : libros) {
            titulosLibros.add(libro.getTitulo());
        }

        // Convertimos la lista de títulos a una cadena separada por comas
        String titulosLibrosString = String.join(", ", titulosLibros);

        bibliotecaDto.setLibros(titulosLibrosString);

        return bibliotecaDto;
    }


}













