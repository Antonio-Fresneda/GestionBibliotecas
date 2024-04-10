package com.gestion.test;


import com.gestion.controller.BibliotecaRestController;
import com.gestion.dto.BibliotecaDto;
import com.gestion.entities.*;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BibliotecaRestControllerTest {
}
/*
    @Mock
    private BibliotecaRepository bibliotecaRepository;

    @InjectMocks
    private BibliotecaRestController bibliotecaRestController;

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private BibliotecaLibroRepository bibliotecaLibroRepository;


    @Test
    public void testGetAll() {

        List<Biblioteca> bibliotecas = new ArrayList<>();
        bibliotecas.add(new Biblioteca(1L, "Biblioteca Central", "123 Main St", "123-456-7890", "info@bibliotecacentral.com", "www.bibliotecacentral.com"));
        bibliotecas.add(new Biblioteca(2L, "Biblioteca Municipal", "456 Elm St", "987-654-3210", "info@bibliotecamunicipal.com", "www.bibliotecamunicipal.com"));

        when(bibliotecaRepository.findAll()).thenReturn(bibliotecas);

        List<BibliotecaDto> result = bibliotecaRestController.getAll();

        assertEquals(bibliotecas.size(), result.size());

    }
}
   /* @Test
    public void testGet() {
        // Arrange
        long id = 1L;
        Biblioteca biblioteca = new Biblioteca(id, "Biblioteca Central", "123 Main St", "123-456-7890", "info@bibliotecacentral.com", "www.bibliotecacentral.com");
        when(bibliotecaRepository.findById(id)).thenReturn(Optional.of(biblioteca));

        // Act
        BibliotecaDto result = bibliotecaRestController.get(id);

        // Assert
        assertNotNull(result);
        assertEquals(biblioteca.getId(), result.getId());
        assertEquals(biblioteca.getNombre(), result.getNombre());
        assertEquals(biblioteca.getDireccion(), result.getDireccion());
        assertEquals(biblioteca.getTelefono(), result.getTelefono());
        assertEquals(biblioteca.getEmail(), result.getEmail());
        assertEquals(biblioteca.getSitioWeb(), result.getSitioWeb());
    }

    @Test
    public void testGet_BibliotecaNotFound() {
        // Arrange
        long id = 1L;
        when(bibliotecaRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BibliotecaNotFoundException.class, () -> bibliotecaRestController.get(id));
    }

    @Test
    public void testDeleteExistingBiblioteca() {
        long idToDelete = 1L;

        // Mock del método deleteById para simular una eliminación exitosa
        doNothing().when(bibliotecaRepository).deleteById(idToDelete);

        // Llamada al método del controlador
        ResponseEntity<?> responseEntity = bibliotecaRestController.delete(idToDelete);

        // Comprobación del resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testDeleteNonExistingBiblioteca() {
        long id = 1L;

        // Configura el mock para que devuelva un Optional vacío cuando se llame a findById con el ID esperado
        when(bibliotecaRepository.findById(id)).thenReturn(Optional.empty());

        // Verifica que al llamar al método delete con el ID proporcionado, se lance la excepción BibliotecaNotFoundException
        assertThrows(BibliotecaNotFoundException.class, () -> {
            bibliotecaRestController.delete(id);
        });

        // Verifica que el método delete del repositorio nunca se llame
        verify(bibliotecaRepository, never()).deleteById(anyLong());
    }






}

 */
