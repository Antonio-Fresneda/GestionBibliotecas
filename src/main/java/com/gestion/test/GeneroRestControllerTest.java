package com.gestion.test;

import com.gestion.controller.GeneroRestController;
import com.gestion.dto.GeneroDto;
import com.gestion.entities.Genero;
import com.gestion.entities.Genero;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.GeneroRepository;
import com.gestion.search.BusquedaLibroRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class GeneroRestControllerTest {
    @Mock
    private GeneroRepository generoRepository;

    @InjectMocks
    private GeneroRestController generoRestController;

    @Autowired
    private EntityManager entityManager;



    @Test
    public void testList() {
        // Datos de ejemplo
        Genero genero1 = new Genero();
        genero1.setId(1L);
        genero1.setNombre("Acción");
        genero1.setDescripcion("Películas llenas de acción y emoción.");
        genero1.setEdadRecomendada(16);
        genero1.setUrlWikipedia("https://es.wikipedia.org/wiki/Acci%C3%B3n_(g%C3%A9nero)");

        Genero genero2 = new Genero();
        genero2.setId(2L);
        genero2.setNombre("Comedia");
        genero2.setDescripcion("Películas que te harán reír.");
        genero2.setEdadRecomendada(12);
        genero2.setUrlWikipedia("https://es.wikipedia.org/wiki/Comedia");

        List<Genero> generos = new ArrayList<>();
        generos.add(genero1);
        generos.add(genero2);

        // Mock del método findAll() del repositorio
        when(generoRepository.findAll()).thenReturn(generos);

        // Llamada al método del controlador
        List<GeneroDto> resultado = generoRestController.list();


        assertEquals(generos.size(), resultado.size());
        for (int i = 0; i < generos.size(); i++) {
            assertEquals(generos.get(i).getNombre(), resultado.get(i).getNombre());
            assertEquals(generos.get(i).getDescripcion(), resultado.get(i).getDescripcion());
            assertEquals(generos.get(i).getEdadRecomendada(), resultado.get(i).getEdadRecomendada());
            assertEquals(generos.get(i).getUrlWikipedia(), resultado.get(i).getUrlWikipedia());
        }
    }

    @Test
    public void testGetExistingGenero() {
        // Datos de ejemplo
        Genero genero = new Genero();
        genero.setId(1L);
        genero.setNombre("Acción");
        genero.setDescripcion("Películas llenas de acción y emoción.");
        genero.setEdadRecomendada(16);
        genero.setUrlWikipedia("https://es.wikipedia.org/wiki/Acci%C3%B3n_(g%C3%A9nero)");

        // Mock del método findById() del repositorio para un género existente
        when(generoRepository.findById(1L)).thenReturn(Optional.of(genero));

        // Llamada al método del controlador
        GeneroDto resultado = generoRestController.get(1L);

        // Comprobación del resultado
        assertEquals(genero.getNombre(), resultado.getNombre());
        assertEquals(genero.getDescripcion(), resultado.getDescripcion());
        assertEquals(genero.getEdadRecomendada(), resultado.getEdadRecomendada());
        assertEquals(genero.getUrlWikipedia(), resultado.getUrlWikipedia());
    }

    @Test
    public void testGetNonExistingGenero() {
        // Mock del método findById() del repositorio para un género inexistente
        when(generoRepository.findById(2L)).thenReturn(Optional.empty());

        // Verificación de que se lanza la excepción apropiada cuando el género no existe
        BibliotecaNotFoundException exception = assertThrows(BibliotecaNotFoundException.class, () -> generoRestController.get(2L));
        assertEquals("Genero not found with id: 2", exception.getMessage());
    }

    @Test
    public void testPutNonExistingGenero() {
        // Simulación de un género inexistente en el repositorio
        when(generoRepository.findById(2L)).thenReturn(Optional.empty());

        Genero generoActualizado = new Genero();
        generoActualizado.setNombre("Aventura");
        generoActualizado.setDescripcion("Películas llenas de aventuras.");
        generoActualizado.setEdadRecomendada(12);
        generoActualizado.setUrlWikipedia("https://es.wikipedia.org/wiki/Aventura_(g%C3%A9nero)");

        // Verificación de que se lanza la excepción apropiada cuando el género no existe
        BibliotecaNotFoundException exception = assertThrows(BibliotecaNotFoundException.class, () -> generoRestController.put(2L, generoActualizado));
        assertEquals("Genero not found with id: 2", exception.getMessage());
    }
    @Test
    public void testDelete() {
        // Establecer un ID para el género que se intentará eliminar
        long generoId = 1L;

        // Mockear el comportamiento del repositorio para simular la búsqueda del género
        Genero genero = new Genero();
        when(generoRepository.findById(generoId)).thenReturn(Optional.of(genero));

        // Llamar al método delete del controlador
        ResponseEntity<?> response = generoRestController.delete(generoId);

        // Verificar que se haya llamado al método findById del repositorio con el ID correcto
        verify(generoRepository, times(1)).findById(generoId);

        // Verificar que se haya llamado al método delete del repositorio con el objeto género correcto
        verify(generoRepository, times(1)).delete(genero);

        // Verificar que el resultado devuelto es el esperado
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue()); // O cualquier otro código de estado esperado
        // Aquí puedes agregar más aserciones si es necesario
    }
    /*@Test
    public void testGetGeneros() {
        // Crear una lista de géneros de ejemplo
        List<Genero> generos = new ArrayList<>();
        Genero genero1 = new Genero();
        genero1.setNombre("Acción");
        genero1.setDescripcion("Películas de acción");
        genero1.setEdadRecomendada(18);
        genero1.setUrlWikipedia("https://es.wikipedia.org/wiki/Acci%C3%B3n");
        generos.add(genero1);

        Genero genero2 = new Genero();
        genero2.setNombre("Comedia");
        genero2.setDescripcion("Películas cómicas");
        genero2.setEdadRecomendada(12);
        genero2.setUrlWikipedia("https://es.wikipedia.org/wiki/Comedia");
        generos.add(genero2);

        // Mockear el comportamiento del repositorio para simular la búsqueda de géneros
        when(generoRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(generos));

        // Llamar al método getGeneros del controlador sin ningún parámetro de búsqueda
        Page<GeneroDto> generosPage = generoRestController.getGeneros(null, null, null, null, "asc", 0, 10);

        // Verificar que se haya llamado al método findAll del repositorio con la configuración correcta
        verify(generoRepository, times(1)).findAll(any(PageRequest.class));

        // Verificar que se devuelva la lista de géneros correctamente convertida a DTO
        assertNotNull(generosPage);
        assertEquals(2, generosPage.getContent().size());
        // Aquí puedes realizar más verificaciones sobre los resultados si es necesario
    }

     */
    @Test
    public void testPostGenero() {
        // Arrange
        Genero genero = new Genero();
        genero.setNombre("Acción");
        genero.setDescripcion("Género de películas de acción");
        genero.setEdadRecomendada(16);
        genero.setUrlWikipedia("https://es.wikipedia.org/wiki/G%C3%A9nero_cinematogr%C3%A1fico");

        Genero generoGuardado = new Genero();
        generoGuardado.setId(1L);
        generoGuardado.setNombre("Acción");
        generoGuardado.setDescripcion("Género de películas de acción");
        generoGuardado.setEdadRecomendada(16);
        generoGuardado.setUrlWikipedia("https://es.wikipedia.org/wiki/G%C3%A9nero_cinematogr%C3%A1fico");

        when(generoRepository.save(genero)).thenReturn(generoGuardado);

        // Act
        ResponseEntity<?> responseEntity = generoRestController.post(genero);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(generoGuardado, responseEntity.getBody());

        verify(generoRepository, times(1)).save(genero);
    }

    @Test
    void testDeleteGeneroNotFound() {
        // Arrange
        long id = 1L;
        when(generoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        BibliotecaNotFoundException exception = assertThrows(BibliotecaNotFoundException.class,
                () -> generoRestController.delete(id));
        assertEquals("Genero not found with id: " + id, exception.getMessage());
    }





}





