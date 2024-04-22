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
    /* @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Biblioteca input) {
        Biblioteca biblioteca = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new BibliotecaNotFoundException("Biblioteca not found with id: " + id));
        biblioteca.setNombre(input.getNombre());
        biblioteca.setDireccion(input.getDireccion());
        biblioteca.setTelefono(input.getTelefono());
        biblioteca.setEmail(input.getEmail());
        biblioteca.setSitioWeb(input.getSitioWeb());
        Biblioteca savedBiblioteca = bibliotecaRepository.save(biblioteca);
        return ResponseEntity.ok(savedBiblioteca);
    }*/



    /*@PostMapping("/crear")
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

     */


    @PostMapping()
    public ResponseEntity<Biblioteca> crearBiblioteca(@RequestBody Biblioteca biblioteca) {

        if (biblioteca.getId() == null || biblioteca.getId() == 0) {
            biblioteca = bibliotecaRepository.save(biblioteca);
        }

        Set<Libro> libros = new HashSet<>();

        // Verificar si la colección de libros no es nula antes de iterar sobre ella
        if (biblioteca.getLibros() != null) {
            for (Libro libro : biblioteca.getLibros()) {
                Libro existingLibro = libroRepository.findByTitulo(libro.getTitulo());

                if (existingLibro == null) {
                    Autor autor = libro.getAutor();
                    if (autor != null && autor.getNombre() != null && autor.getFechaNacimiento() != null && autor.getNacionalidad() != null) {
                        // Comprobar si el autor ya existe en la base de datos por nombre, fecha de nacimiento y nacionalidad
                        List<Autor> existingAutorList = autorRepository.findByNombreAndFechaNacimientoAndNacionalidad(autor.getNombre(), autor.getFechaNacimiento(), autor.getNacionalidad());
                        if (!existingAutorList.isEmpty()) {
                            // Si el autor ya existe, asignamos el existente en lugar de crear uno nuevo
                            autor = existingAutorList.get(0); // Utilizamos el autor existente
                        } else {
                            // Si no existe, lo guardamos como un nuevo autor
                            autor = autorRepository.save(autor);
                        }
                        // Asignar el autor al libro
                        libro.setAutor(autor);
                    }

                    Genero genero = libro.getGenero();
                    if (genero != null && genero.getNombre() != null && !genero.getNombre().isEmpty()) {
                        // Buscar el género por nombre en la base de datos
                        Genero existingGenero = generoRepository.findByNombre(genero.getNombre());

                        if (existingGenero != null) {
                            // Si el género ya existe en la base de datos, lo asignamos al libro
                            genero = existingGenero; // Utilizamos el género existente
                        } else {
                            // Si el género no existe en la base de datos, lo guardamos y luego lo asignamos al libro
                            genero = generoRepository.save(genero);
                        }
                        // Asignar el género al libro
                        libro.setGenero(genero);
                    }

                    // Guardar el libro
                    libro = libroRepository.save(libro);
                }

                libros.add(libro);
            }
        }

        biblioteca.setLibros(libros);
        biblioteca = bibliotecaRepository.save(biblioteca);

        return new ResponseEntity<>(biblioteca, HttpStatus.CREATED);
    }



    /*@DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBiblioteca(@PathVariable(name = "id") long id) {
        Optional<Biblioteca> bibliotecaOptional = bibliotecaRepository.findById(id);
        if (bibliotecaOptional.isEmpty()) {
            throw new BibliotecaNotFoundException("Biblioteca with id " + id + " not found");
        }
        bibliotecaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBiblioteca(@PathVariable(name = "id") long id) {
        Optional<Biblioteca> bibliotecaOptional = bibliotecaRepository.findById(id);
        if (bibliotecaOptional.isEmpty()) {
            throw new BibliotecaNotFoundException("Biblioteca with id " + id + " not found");
        }

        // Eliminar la relación entre la biblioteca y los libros
        Biblioteca biblioteca = bibliotecaOptional.get();
        biblioteca.getLibros().clear();
        bibliotecaRepository.save(biblioteca);

        // Eliminar la biblioteca
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
        //bibliotecaDto.setLibros(biblioteca.getLibros());
        return bibliotecaDto;
    }

    @Autowired
    private EntityManager entityManager;

    /*@PostMapping("/buscar-bibliotecas")
    public List<BibliotecaDto> buscarBibliotecas(@RequestBody BusquedaLibroRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Biblioteca> criteriaQuery = criteriaBuilder.createQuery(Biblioteca.class);
        Root<Biblioteca> root = criteriaQuery.from(Biblioteca.class);


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
        List<Biblioteca> bibliotecas = entityManager.createQuery(criteriaQuery)
                .setFirstResult(request.getPage().getPageIndex() * request.getPage().getPageSize())
                .setMaxResults(request.getPage().getPageSize())
                .getResultList();

        // Convertir las bibliotecas a BibliotecaDto
        List<BibliotecaDto> bibliotecasDto = new ArrayList<>();
        for (Biblioteca biblioteca : bibliotecas) {
            bibliotecasDto.add(convertirABibliotecaDto(biblioteca));
        }

        return bibliotecasDto;
    }

     */
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
            bibliotecasDto.add(convertirABibliotecaDto(biblioteca));
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

    private BibliotecaDto convertirABibliotecaDto(Biblioteca biblioteca) {
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













