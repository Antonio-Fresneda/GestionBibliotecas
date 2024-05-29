package com.gestion.controller;

import com.gestion.dto.AutorDto;
import com.gestion.dto.GeneroDto;
import com.gestion.entities.Genero;
import com.gestion.entities.Libro;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.GeneroRepository;
import com.gestion.repository.LibroRepository;
import com.gestion.search.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genero")
public class GeneroRestController {

    @Autowired
    GeneroRepository generoRepository;

    @Autowired
    LibroRepository libroRepository;

    @PreAuthorize("hasAnyAuthority('LEER_GENERO','ESCRIBIR_GENERO')")
    @GetMapping()
    public List<GeneroDto> list() {
        List<Genero> generos = generoRepository.findAll();
        return generos.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = generoRepository.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}")
    public GeneroDto get(@PathVariable(name = "id") long id) {
        Genero genero = generoRepository.findById(id).orElse(null);
        if (genero != null) {
            return convertToDto(genero);
        } else {
            throw new BibliotecaNotFoundException("Genero not found with id: " + id);
        }
    }

    @PreAuthorize("hasAuthority('ESCRIBIR_GENERO')")
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
        return ResponseEntity.ok(convertToDto(save));
    }

    @PreAuthorize("hasAuthority('ESCRIBIR_GENERO')")
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Genero input) {
        Genero save = generoRepository.save(input);
        return ResponseEntity.ok((save));
    }

    @PreAuthorize("hasAuthority('ESCRIBIR_GENERO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Genero> findById = generoRepository.findById(id);
        if (findById.isPresent()) {
            Genero genero = findById.get();
            generoRepository.delete(genero);

            return ResponseEntity.ok().build();
        } else {
            throw new BibliotecaNotFoundException("Genero not found with id: " + id);
        }
    }

    @PreAuthorize("hasAnyAuthority('ESCRIBIR_GENERO','LEER_GENERO')")
    @GetMapping("/generos")
    public List<GeneroDto> getGeneros(
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

        Page<Genero> generosPage;

        if (nombre != null && descripcion != null && edadRecomendada != null && urlWikipedia != null) {
            generosPage = generoRepository.findAllByNombreContainingAndDescripcionContainingAndEdadRecomendadaAndUrlWikipediaContaining(nombre, descripcion, edadRecomendada, urlWikipedia, pageRequest);
        } else if (nombre != null && descripcion != null && edadRecomendada != null) {
            generosPage = generoRepository.findAllByNombreContainingAndDescripcionContainingAndEdadRecomendada(nombre, descripcion, edadRecomendada, pageRequest);
        } else if (nombre != null && descripcion != null && urlWikipedia != null) {
            generosPage = generoRepository.findAllByNombreContainingAndDescripcionContainingAndUrlWikipediaContaining(nombre, descripcion, urlWikipedia, pageRequest);
        } else if (nombre != null && edadRecomendada != null && urlWikipedia != null) {
            generosPage = generoRepository.findAllByNombreContainingAndEdadRecomendadaAndUrlWikipediaContaining(nombre, edadRecomendada, urlWikipedia, pageRequest);
        } else if (descripcion != null && edadRecomendada != null && urlWikipedia != null) {
            generosPage = generoRepository.findAllByDescripcionContainingAndEdadRecomendadaAndUrlWikipediaContaining(descripcion, edadRecomendada, urlWikipedia, pageRequest);
        } else if (nombre != null && descripcion != null) {
            generosPage = generoRepository.findAllByNombreContainingAndDescripcionContaining(nombre, descripcion, pageRequest);
        } else if (nombre != null && edadRecomendada != null) {
            generosPage = generoRepository.findAllByNombreContainingAndEdadRecomendada(nombre, edadRecomendada, pageRequest);
        } else if (nombre != null && urlWikipedia != null) {
            generosPage = generoRepository.findAllByNombreContainingAndUrlWikipediaContaining(nombre, urlWikipedia, pageRequest);
        } else if (descripcion != null && edadRecomendada != null) {
            generosPage = generoRepository.findAllByDescripcionContainingAndEdadRecomendada(descripcion, edadRecomendada, pageRequest);
        } else if (descripcion != null && urlWikipedia != null) {
            generosPage = generoRepository.findAllByDescripcionContainingAndUrlWikipediaContaining(descripcion, urlWikipedia, pageRequest);
        } else if (edadRecomendada != null && urlWikipedia != null) {
            generosPage = generoRepository.findAllByEdadRecomendadaAndUrlWikipediaContaining(edadRecomendada, urlWikipedia, pageRequest);
        } else if (nombre != null) {
            generosPage = generoRepository.findAllByNombreContaining(nombre, pageRequest);
        } else if (descripcion != null) {
            generosPage = generoRepository.findAllByDescripcionContaining(descripcion, pageRequest);
        } else if (edadRecomendada != null) {
            generosPage = generoRepository.findAllByEdadRecomendada(edadRecomendada, pageRequest);
        } else if (urlWikipedia != null) {
            generosPage = generoRepository.findAllByUrlWikipediaContaining(urlWikipedia, pageRequest);
        } else {
            generosPage = generoRepository.findAll(pageRequest);
        }
        List<GeneroDto> generoDtoList = generosPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return generoDtoList;


    }
    private GeneroDto convertToDto(Genero genero) {
        GeneroDto generoDto = new GeneroDto();
        generoDto.setId(genero.getId());
        generoDto.setNombre(genero.getNombre());
        generoDto.setDescripcion(genero.getDescripcion());
        generoDto.setEdadRecomendada(genero.getEdadRecomendada());
        generoDto.setUrlWikipedia(genero.getUrlWikipedia());

        return generoDto;
    }


    @Autowired
    private EntityManager entityManager;



    @PreAuthorize("hasAnyAuthority('ESCRIBIR_GENERO','LEER_GENERO')")
    @PostMapping("/buscar-generos")
   public List<GeneroDto> buscarGeneros(@RequestBody BusquedaLibroRequest request) {
       CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
       CriteriaQuery<Genero> criteriaQuery = criteriaBuilder.createQuery(Genero.class);
       Root<Genero> root = criteriaQuery.from(Genero.class);

       List<Predicate> predicates = new ArrayList<>();

       for (SearchCriteria criteria : request.getListSearchCriteria()) {
           String key = criteria.getKey();
           String value = criteria.getValue().toString(); // Convertimos el valor a String

           switch (key) {
               case "nombre":
               case "descripcion":
               case "urlWikipedia":
                   predicates.add(criteriaBuilder.like(root.get(key), "%" + value + "%"));
                   break;
               case "edadRecomendada":
                   try {
                       Long edad = Long.parseLong(value);
                       predicates.add(criteriaBuilder.equal(root.get(key), edad));
                   } catch (NumberFormatException e) {

                   }
                   break;
               default:

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

       List<Genero> generos = entityManager.createQuery(criteriaQuery)
               .setFirstResult(request.getPage().getPageIndex() * request.getPage().getPageSize())
               .setMaxResults(request.getPage().getPageSize())
               .getResultList();

       List<GeneroDto> generosDto = new ArrayList<>();
       for (Genero genero : generos) {
           generosDto.add(convertirAGeneroDto(genero));
       }

       return generosDto;
   }


    private Predicate getPredicate(SearchCriteria criteria, CriteriaBuilder builder, Root<Genero> root) {
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

    private GeneroDto convertirAGeneroDto(Genero genero) {
        GeneroDto generoDto = new GeneroDto();
        generoDto.setId(genero.getId());
        generoDto.setNombre(genero.getNombre());
        generoDto.setDescripcion(genero.getDescripcion());
        generoDto.setEdadRecomendada(genero.getEdadRecomendada());
        generoDto.setUrlWikipedia(genero.getUrlWikipedia());
        return generoDto;
    }

}


