package com.gestion.test;

import com.gestion.controller.GeneroRestController;
import com.gestion.controller.LibroRestController;
import com.gestion.dto.LibroDto;
import com.gestion.entities.Autor;
import com.gestion.entities.Genero;
import com.gestion.entities.Libro;
import com.gestion.entities.LibroGenero;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.AutorRepository;
import com.gestion.repository.GeneroRepository;
import com.gestion.repository.LibroRepository;
import com.gestion.search.BusquedaLibroRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibroRestControllerTest {
    @Mock
    private LibroRepository libroRepository;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private GeneroRepository generoRepository;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private LibroRestController libroRestController;


    @Test
    public void testList() {
        // Arrange
        List<Libro> libros = new ArrayList<>();
        libros.add(new Libro(1L, "Libro 1", 2022, "1234567890"));
        libros.add(new Libro(2L, "Libro 2", 2023, "0987654321"));

        when(libroRepository.findAll()).thenReturn(libros);
        List<LibroDto> result = libroRestController.list();

        assertEquals(2, result.size());
        assertEquals("Libro 1", result.get(0).getTitulo());
        assertEquals(2022, result.get(0).getAnoPublicacion());
        assertEquals("1234567890", result.get(0).getIsbn());
        assertEquals("Libro 2", result.get(1).getTitulo());
        assertEquals(2023, result.get(1).getAnoPublicacion());
        assertEquals("0987654321", result.get(1).getIsbn());

    }

    @Test
    public void testGet() {
        long libroId = 1L;
        Libro libro = new Libro(libroId, "Libro 1", 2022, "1234567890");
        when(libroRepository.findById(libroId)).thenReturn(Optional.of(libro));

        LibroDto result = libroRestController.get(libroId);

        assertEquals(libro.getTitulo(), result.getTitulo());
        assertEquals(libro.getAnoPublicacion(), result.getAnoPublicacion());
        assertEquals(libro.getIsbn(), result.getIsbn());

    }
    @Test
    public void testPut() {

        long libroId = 1L;
        Libro existingLibro = new Libro(libroId, "Existing Title", 2000, "1234567890");
        Libro updatedLibro = new Libro(libroId, "Updated Title", 2022, "0987654321");

        when(libroRepository.findById(libroId)).thenReturn(Optional.of(existingLibro));
        when(libroRepository.save(any(Libro.class))).thenReturn(updatedLibro);

        ResponseEntity<?> responseEntity = libroRestController.put(libroId, updatedLibro);


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedLibro.getTitulo(), existingLibro.getTitulo());
        assertEquals(updatedLibro.getAnoPublicacion(), existingLibro.getAnoPublicacion());
        assertEquals(updatedLibro.getIsbn(), existingLibro.getIsbn());
    }

    @Test
    public void testPutNonExistingLibro() {
        // Simulación de un libro inexistente en el repositorio
        long libroId = 2L;
        Libro updatedLibro = new Libro(libroId, "Updated Title", 2022, "0987654321");

        when(libroRepository.findById(libroId)).thenReturn(Optional.empty());

        // Verificación de que se lanza la excepción apropiada cuando el libro no existe
        // Aquí necesitas cambiar libroRestController por el controlador adecuado de libros
        // Supongamos que el controlador de libros se llama libroController
        BibliotecaNotFoundException exception = assertThrows(BibliotecaNotFoundException.class, () -> libroRestController.put(libroId, updatedLibro));
        assertEquals("Libro not found with id: " + libroId, exception.getMessage());
    }

    /*@Test
    public void testCrearLibro() {
        // Datos de ejemplo para el libro
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Autor de Prueba");

        Genero genero = new Genero();
        genero.setId(1L);
        genero.setNombre("Genero de Prueba");

        LibroGenero libroGenero = new LibroGenero();
        libroGenero.setGenero(genero);

        Libro libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Libro de Prueba");
        libro.setAutor(autor);
        libro.setGeneros(Collections.singletonList(libroGenero));

        // Mock del método save() del repositorio para el libro de ejemplo
        when(libroRepository.save(libro)).thenReturn(libro);

        // Llamada al método del controlador
        ResponseEntity<Libro> responseEntity = libroRestController.crearLibro(libro);

        // Comprobación del resultado
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(libro, responseEntity.getBody());
    }

     */

    /*@Test
    public void testDeleteExistingLibro() {
        // Arrange
        long id = 1L;
        Libro libro = new Libro();
        libro.setId(id);

        when(libroRepository.findById(id)).thenReturn(Optional.of(libro));

        // Act
        ResponseEntity<?> responseEntity = libroRestController.delete(id);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(libroRepository, times(1)).delete(libro);
    }



    @Test
    public void testDeleteNonExistingLibro() {

        long id = 1L;

        when(libroRepository.findById(id)).thenReturn(Optional.empty());


        assertThrows(BibliotecaNotFoundException.class, () -> {
            libroRestController.delete(id);
        });
        verify(libroRepository, never()).delete(any());
    }
     */

    /*@Test
    public void testGetLibros() {
        String titulo = "titulo";
        Integer anoPublicacion = 2022;
        String isbn = "isbn";
        Long autorId = 1L;
        String direccion = "asc";
        int pagina = 0;
        int tamanoPagina = 10;

        PageRequest pageRequest = PageRequest.of(pagina, tamanoPagina, Sort.by(Sort.Direction.ASC, "titulo"));
        List<Libro> libros = new ArrayList<>();
        libros.add(new Libro();
        Page<Libro> librosPage = new PageImpl<>(libros, pageRequest, libros.size());

        when(libroRepository.findAllByTituloContainingAndAnoPublicacionAndIsbnContainingAndAutorId(titulo, anoPublicacion, isbn, autorId, pageRequest))
                .thenReturn(librosPage);

        Page<LibroDto> result = libroRestController.getLibros(titulo, anoPublicacion, isbn, autorId, direccion, generoIds,pagina, tamanoPagina);

        assertEquals(libros.size(), result.getContent().size());

    }
    */


}
