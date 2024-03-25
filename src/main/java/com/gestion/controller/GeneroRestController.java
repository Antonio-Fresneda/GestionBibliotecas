package com.gestion.controller;

import com.gestion.dto.GeneroDto;
import com.gestion.entities.Genero;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.GeneroRepository;
import com.gestion.search.OrderCriteria;
import com.gestion.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genero")
public class GeneroRestController {

    @Autowired
    GeneroRepository generoRepository;

    private GeneroDto convertToDto(Genero genero) {
        GeneroDto generoDto = new GeneroDto();
        generoDto.setId(genero.getId());
        generoDto.setNombre(genero.getNombre());
        generoDto.setDescripcion(genero.getDescripcion());
        generoDto.setEdadRecomendada(genero.getEdadRecomendada());
        generoDto.setUrlWikipedia(genero.getUrlWikipedia());

        return generoDto;
    }

    @GetMapping()
    public List<GeneroDto> list() {
        List<Genero> generos = generoRepository.findAll();
        return generos.stream().map(this::convertToDto).collect(Collectors.toList());
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

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Genero input) {
        Genero save = generoRepository.save(input);
        return ResponseEntity.ok(convertToDto(save));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Genero> findById = generoRepository.findById(id);
        if (findById.isPresent()) {
            generoRepository.delete(findById.get());
            return ResponseEntity.ok().build();
        } else {
            throw new BibliotecaNotFoundException("Genero not found with id: " + id);
        }
    }

    @GetMapping("/generos")
    public Page<GeneroDto> getGeneros(
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

        return generosPage.map(this::convertToDto);
    }


    /*@PostMapping("/generos/search")
    public Page<Genero> searchGeneros(@RequestBody SearchRequest searchRequest) {
        Pageable pageRequest;
        Sort sort = null;

        if (!searchRequest.getListOrderCriteria().isEmpty()) {
            sort = Sort.by(searchRequest.getListOrderCriteria().stream()
                    .map(criteria -> Sort.Order.by(criteria.getValuesortOrder()).with(Sort.Direction.valueOf(criteria.getSortBy())))
                    .toArray(Sort.Order[]::new));
        }

        if (sort != null) {
            pageRequest = PageRequest.of(searchRequest.getPage().getPageNumber(), searchRequest.getPage().getPageSize(), sort);
        } else {
            pageRequest = PageRequest.of(searchRequest.getPage().getPageNumber(), searchRequest.getPage().getPageSize());
        }

        String nombre = null;
        Integer edadRecomendada = null;
        String descripcion = null;
        String urlWikipedia = null;

        for (SearchCriteria criteria : searchRequest.getListSearchCriteria()) {
            if (criteria.getKey().equals("nombre")) {
                nombre = criteria.getValue();
            } else if (criteria.getKey().equals("edadRecomendada")) {
                edadRecomendada = Integer.parseInt(criteria.getValue());
            } else if (criteria.getKey().equals("descripcion")) {
                descripcion = criteria.getValue();
            } else if (criteria.getKey().equals("urlWikipedia")) {
                urlWikipedia = criteria.getValue();
            }
        }

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

        return generosPage;
    }

     */

    @PostMapping("/generos/search")
    public Page<GeneroDto> searchGeneros(@RequestBody SearchRequest searchRequest) {
        Pageable pageable = getPageableFromRequest(searchRequest);
        Sort sort = getSortFromRequest(searchRequest);

        if (sort != null) {
            return generoRepository.findAllOrderedAndFiltered("nombre", null, pageable)
                    .map(this::convertToDto);
        } else {
            return generoRepository.findAll(pageable)
                    .map(this::convertToDto);
        }
    }

    private Pageable getPageableFromRequest(SearchRequest searchRequest) {
        int pageIndex = searchRequest.getPage().getPageNumber();
        int pageSize = searchRequest.getPage().getPageSize();
        return PageRequest.of(pageIndex, pageSize);
    }

    private Sort getSortFromRequest(SearchRequest searchRequest) {
        List<OrderCriteria> orderCriteriaList = searchRequest.getListOrderCriteria();
        if (orderCriteriaList == null || orderCriteriaList.isEmpty()) {
            return null; // No hay criterios de ordenación
        }

        OrderCriteria orderCriteria = orderCriteriaList.get(0); // Tomamos solo el primer criterio de ordenación
        String sortBy = orderCriteria.getSortBy();
        String valueSortOrder = orderCriteria.getValueSortOrder();
        Sort.Direction direction = Sort.Direction.fromString(valueSortOrder);

        if (sortBy != null && !sortBy.isEmpty()) {
            return Sort.by(direction, sortBy);
        } else {
            return null;
        }
    }

}
