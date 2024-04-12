package com.gestion.controller;

import com.gestion.dto.AutorDto;
import com.gestion.entities.Autor;
import com.gestion.entities.Genero;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.AutorRepository;
import com.gestion.search.BusquedaLibroRequest;
import com.gestion.search.OrderCriteria;
import com.gestion.search.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/autor")
public class AutorRestController {

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
            autorRepository.deleteLibrosByAutorId(autor.getId());
            autorRepository.delete(autor);
            return ResponseEntity.ok().build();
        } else {
            throw new BibliotecaNotFoundException("Autor not found with id: " + id);
        }
    }






    @GetMapping("/autores")
    public List<AutorDto> getAutores(
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

        Page<Autor> autoresPage;

        if (nombre != null && !nombre.isEmpty() && fechaNacimiento != null && nacionalidad != null && !nacionalidad.isEmpty()) {
            autoresPage = autorRepository.findAllByNombreAndFechaNacimientoAndNacionalidad(nombre, fechaNacimiento, nacionalidad, pageRequest);
        } else if (nombre != null && !nombre.isEmpty() && fechaNacimiento != null) {
            autoresPage = autorRepository.findAllByNombreAndFechaNacimiento(nombre, fechaNacimiento, pageRequest);
        } else if (nombre != null && !nombre.isEmpty() && nacionalidad != null && !nacionalidad.isEmpty()) {
            autoresPage = autorRepository.findAllByNombreAndNacionalidad(nombre, nacionalidad, pageRequest);
        } else if (fechaNacimiento != null && nacionalidad != null && !nacionalidad.isEmpty()) {
            autoresPage = autorRepository.findAllByFechaNacimientoAndNacionalidad(fechaNacimiento, nacionalidad, pageRequest);
        } else if (nombre != null && !nombre.isEmpty()) {
            autoresPage = autorRepository.findAllByNombre(nombre, pageRequest);
        } else if (fechaNacimiento != null) {
            autoresPage = autorRepository.findAllByFechaNacimiento(fechaNacimiento, pageRequest);
        } else if (nacionalidad != null && !nacionalidad.isEmpty()) {
            autoresPage = autorRepository.findAllByNacionalidad(nacionalidad, pageRequest);
        } else {
            autoresPage = autorRepository.findAll(pageRequest);
        }

        List<AutorDto> autoresDtoList = autoresPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return autoresDtoList;
    }

    private AutorDto convertToDto(Autor autor) {
        AutorDto autorDto = new AutorDto();
        autorDto.setId(autor.getId());
        autorDto.setNombre(autor.getNombre());
        autorDto.setFechaNacimiento(autor.getFechaNacimiento());
        autorDto.setNacionalidad(autor.getNacionalidad());
        return autorDto;
    }


    @Autowired
    private EntityManager entityManager;

    @PostMapping("/buscar-autores")
    public List<AutorDto> buscarAutores(@RequestBody BusquedaLibroRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Autor> criteriaQuery = criteriaBuilder.createQuery(Autor.class);
        Root<Autor> root = criteriaQuery.from(Autor.class);

        Predicate predicate = criteriaBuilder.conjunction();
        for (SearchCriteria criteria : request.getListSearchCriteria()) {
            predicate = criteriaBuilder.and(predicate, getPredicate(criteria, criteriaBuilder, root));
        }
        criteriaQuery.where(predicate);

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


        List<Autor> autores = entityManager.createQuery(criteriaQuery)
                .setFirstResult(request.getPage().getPageIndex() * request.getPage().getPageSize())
                .setMaxResults(request.getPage().getPageSize())
                .getResultList();

        // Convertir los autores a AutorDto
        List<AutorDto> autoresDto = new ArrayList<>();
        for (Autor autor : autores) {
            autoresDto.add(convertirAAutorDto(autor));
        }

        return autoresDto;
    }

    /*private Predicate getPredicate(SearchCriteria criteria, CriteriaBuilder builder, Root<Autor> root) {
        switch (criteria.getOperation()) {
            case "EQUALS","equals":
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date fecha = sdf.parse(criteria.getValue());
                    return builder.equal(root.get(criteria.getKey()), fecha);
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Manejar el error según sea necesario
                    return null;
                }
            case "LESS_THAN","less_than":
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date fecha = sdf.parse(criteria.getValue());
                    return builder.lessThan(root.get(criteria.getKey()), fecha);
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Manejar el error según sea necesario
                    return null;
                }
            case "GREATER_THAN","greather_than":
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date fecha = sdf.parse(criteria.getValue());
                    return builder.greaterThan(root.get(criteria.getKey()), fecha);
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Manejar el error según sea necesario
                    return null;
                }
            case "CONTAINS" ,"contains":
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

     */
    private Predicate getPredicate(SearchCriteria criteria, CriteriaBuilder builder, Root<Autor> root) {
        switch (criteria.getOperation()) {
            case "EQUALS","equals":
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case "GREATER_THAN","greather_than":
                return builder.greaterThan(root.get(criteria.getKey()), criteria.getValue());
            case "LESS_THAN","less_than":
                return builder.lessThan(root.get(criteria.getKey()), criteria.getValue());
            case "CONTAINS","contains":
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

    private AutorDto convertirAAutorDto(Autor autor) {
        AutorDto autorDto = new AutorDto();
        autorDto.setId(autor.getId());
        autorDto.setNombre(autor.getNombre());
        autorDto.setFechaNacimiento(autor.getFechaNacimiento());
        autorDto.setNacionalidad(autor.getNacionalidad());
        return autorDto;
    }


}
