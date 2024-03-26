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
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BibliotecaRestControllerTest {

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
    @Test
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
    public void testCrearBiblioteca() {
        // Arrange
        Biblioteca biblioteca = new Biblioteca(1, "Biblioteca Test", "123 Oak St", "123-456-7890", "info@bibliotecatest.com", "www.bibliotecatest.com");
        when(bibliotecaRepository.save(any(Biblioteca.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(libroRepository.findByTitulo(anyString())).thenReturn(null);
        when(libroRepository.save(any(Libro.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(autorRepository.save(any(Autor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(generoRepository.save(any(Genero.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bibliotecaLibroRepository.save(any(BibliotecaLibro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<Biblioteca> response = bibliotecaRestController.crearBiblioteca(biblioteca);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Biblioteca createdBiblioteca = response.getBody();
        assertNotNull(createdBiblioteca);
        assertEquals(biblioteca.getNombre(), createdBiblioteca.getNombre());
        assertEquals(biblioteca.getDireccion(), createdBiblioteca.getDireccion());
        assertEquals(biblioteca.getTelefono(), createdBiblioteca.getTelefono());
        assertEquals(biblioteca.getEmail(), createdBiblioteca.getEmail());
        assertEquals(biblioteca.getSitioWeb(), createdBiblioteca.getSitioWeb());
    }


}
