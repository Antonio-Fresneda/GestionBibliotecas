package com.gestion.controller;

import com.gestion.dto.AutorDto;
import com.gestion.entities.Autor;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.AutorRepository;
import com.gestion.search.SearchCriteria;
import com.gestion.search.SearchRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            autorRepository.deleteLibroGeneroByAutorId(autor.getId());
            autorRepository.deleteLibrosByAutorId(autor.getId());
            autorRepository.delete(autor);
            return ResponseEntity.ok().build();
        } else {
            throw new BibliotecaNotFoundException("Autor not found with id: " + id);
        }
    }

    @GetMapping("/autores")
    public Page<AutorDto> getAutores(
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

        return autoresPage.map(this::convertToDto);
    }

    private AutorDto convertToDto(Autor autor) {
        AutorDto autorDto = new AutorDto();
        autorDto.setId(autor.getId());
        autorDto.setNombre(autor.getNombre());
        autorDto.setFechaNacimiento(autor.getFechaNacimiento());
        autorDto.setNacionalidad(autor.getNacionalidad());

        return autorDto;
    }

    @Data
    public class PageRequestDto {

        private int pageIndex;
        private int pageSize;

        public int getPageIndex() {
            return pageIndex;
        }

        public int getPageSize() {
            return pageSize;
        }


    }
   /* @PostMapping("/autores/search")
    public ResponseEntity<Page<AutorDto>> searchAutores(@RequestBody SearchRequest request) {
        List<SearchCriteria> searchCriteria = request.getListSearchCriteria();
        Pageable pageable = PageRequest.of(
                request.getPage().getPageIndex(),
                request.getPage().getPageSize()
        );

        // Realizar la búsqueda con los criterios proporcionados
        Page<Autor> autoresPage = autorRepository.searchAutores(
                searchCriteria.get(0).getKey(),
                searchCriteria.get(0).getValue(),
                pageable
        );


        Page<AutorDto> autoresDtoPage = autoresPage.map(this::convertToDto);

        return new ResponseEntity<>(autoresDtoPage, HttpStatus.OK);
    }

    */


    @PostMapping("/autores/search")
    public Page<Autor> searchAutores(@RequestBody SearchRequest searchRequest) throws ParseException {

        Pageable pageable = PageRequest.of(
                searchRequest.getPage().getPageNumber(),
                searchRequest.getPage().getPageSize(),
                Sort.by(searchRequest.getListOrderCriteria().stream()
                        .map(criteria -> Sort.Order.by(criteria.getSortBy())
                                .with(criteria.getValueSortOrder().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC))
                        .collect(Collectors.toList())
                )
        );

        String nombre = null, nacionalidad = null;
        Date fechaNacimiento = null;

        for (SearchCriteria criteria : searchRequest.getListSearchCriteria()) {
            if (criteria.getKey().equals("nombre")) {
                nombre = criteria.getValue();
            } else if (criteria.getKey().equals("fechaNacimiento")) {
                String fechaNacimientoString = criteria.getValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                fechaNacimiento = dateFormat.parse(fechaNacimientoString);
            } else if (criteria.getKey().equals("nacionalidad")) {
                nacionalidad = criteria.getValue();
            }
        }

        Page<Autor> autores = null;

        if (nombre != null && fechaNacimiento != null && nacionalidad != null) {
            autores = autorRepository.findAllByNombreAndFechaNacimientoAndNacionalidad(nombre, fechaNacimiento, nacionalidad, pageable);
        } else if (nombre != null && fechaNacimiento != null) {
            autores = autorRepository.findAllByNombreAndFechaNacimiento(nombre, fechaNacimiento, pageable);
        } else if (nombre != null && nacionalidad != null) {
            autores = autorRepository.findAllByNombreAndNacionalidad(nombre, nacionalidad, pageable);
        } else if (fechaNacimiento != null && nacionalidad != null) {
            autores = autorRepository.findAllByFechaNacimientoAndNacionalidad(fechaNacimiento, nacionalidad, pageable);
        } else if (nombre != null) {
            autores = autorRepository.findAllByNombre(nombre, pageable);
        } else if (fechaNacimiento != null) {
            autores = autorRepository.findAllByFechaNacimiento(fechaNacimiento, pageable);
        } else if (nacionalidad != null) {
            autores = autorRepository.findAllByNacionalidad(nacionalidad, pageable);
        } else {
            autores = autorRepository.findAll(pageable);
        }

        return autores;
    }


}
