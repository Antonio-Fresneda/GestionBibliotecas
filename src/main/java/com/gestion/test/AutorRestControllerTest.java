package com.gestion.test;


import com.gestion.controller.AutorRestController;
import com.gestion.dto.AutorDto;
import com.gestion.entities.Autor;
import com.gestion.exception.BibliotecaNotFoundException;
import com.gestion.repository.AutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class AutorRestControllerTest {
    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private AutorRestController autorController;



    @Test
    public void testList() {
        // Datos de ejemplo
        Autor autor1 = new Autor();
        autor1.setId(1L);
        autor1.setNombre("Nombre1");
        autor1.setFechaNacimiento(new Date());
        autor1.setNacionalidad("Nacionalidad1");

        Autor autor2 = new Autor();
        autor2.setId(2L);
        autor2.setNombre("Nombre2");
        autor2.setFechaNacimiento(new Date());
        autor2.setNacionalidad("Nacionalidad2");

        List<Autor> listaAutores = new ArrayList<>();
        listaAutores.add(autor1);
        listaAutores.add(autor2);

        // Mock del método findAll() del repositorio
        when(autorRepository.findAll()).thenReturn(listaAutores);

        // Llamada al método del controlador
        List<Autor> resultado = autorController.list();

        // Comprobación del resultado
        assertEquals(listaAutores.size(), resultado.size());
        assertEquals(listaAutores.get(0).getNombre(), resultado.get(0).getNombre());
        assertEquals(listaAutores.get(0).getFechaNacimiento(), resultado.get(0).getFechaNacimiento());
        assertEquals(listaAutores.get(0).getNacionalidad(), resultado.get(0).getNacionalidad());
        assertEquals(listaAutores.get(1).getNombre(), resultado.get(1).getNombre());
        assertEquals(listaAutores.get(1).getFechaNacimiento(), resultado.get(1).getFechaNacimiento());
        assertEquals(listaAutores.get(1).getNacionalidad(), resultado.get(1).getNacionalidad());
    }
    @Test
    public void testGetExistingAutor() {
        // Datos de ejemplo
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Nombre1");
        autor.setFechaNacimiento(new Date());
        autor.setNacionalidad("Nacionalidad1");

        // Mock del método findById() del repositorio para un autor existente
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

        // Llamada al método del controlador
        Autor resultado = autorController.get(1L);

        // Comprobación del resultado
        assertEquals(autor.getId(), resultado.getId());
        assertEquals(autor.getNombre(), resultado.getNombre());
        assertEquals(autor.getFechaNacimiento(), resultado.getFechaNacimiento());
        assertEquals(autor.getNacionalidad(), resultado.getNacionalidad());
    }

    @Test
    public void testGetNonExistingAutor() {
        // Mock del método findById() del repositorio para un autor inexistente
        when(autorRepository.findById(2L)).thenReturn(Optional.empty());

        // Verificación de que se lanza la excepción apropiada cuando el autor no existe
        assertThrows(BibliotecaNotFoundException.class, () -> autorController.get(2L));
    }

    @Test
    public void testPost() {
        // Datos de ejemplo
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Nombre1");
        autor.setFechaNacimiento(new Date());
        autor.setNacionalidad("Nacionalidad1");

        // Mock del método save() del repositorio para el autor de ejemplo
        when(autorRepository.save(autor)).thenReturn(autor);

        // Llamada al método del controlador
        ResponseEntity<?> responseEntity = autorController.post(autor);

        // Comprobación del resultado
        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(autor, responseEntity.getBody());
    }
   /* @Test
    public void testDeleteExistingAutor() {
        // Datos de ejemplo
        Autor autor = new Autor();
        autor.setId(1L);

        // Simulación de un autor existente en el repositorio
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

        // Llamada al método del controlador
        ResponseEntity<?> responseEntity = autorController.delete(1L);

        // Comprobación del resultado
        assertEquals(OK, responseEntity.getStatusCode());
        verify(autorRepository).deleteLibroGeneroByAutorId(1L);
        verify(autorRepository).deleteLibrosByAutorId(1L);
        verify(autorRepository).delete(autor);
    }

    */

    @Test
    public void testDeleteNonExistingAutor() {
        // Simulación de un autor inexistente en el repositorio
        when(autorRepository.findById(2L)).thenReturn(Optional.empty());

        // Verificación de que se lanza la excepción apropiada cuando el autor no existe
        BibliotecaNotFoundException exception = assertThrows(BibliotecaNotFoundException.class, () -> autorController.delete(2L));
        assertEquals("Autor not found with id: 2", exception.getMessage());
    }
   /* @Test
    public void testPutExistingAutor() {
        // Datos de ejemplo
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Nombre1");
        autor.setFechaNacimiento(new Date());
        autor.setNacionalidad("Nacionalidad1");

        // Simulación de un autor existente en el repositorio
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

        Autor autorActualizado = new Autor();
        autorActualizado.setNombre("NuevoNombre");
        autorActualizado.setFechaNacimiento(new Date());
        autorActualizado.setNacionalidad("NuevaNacionalidad");

        // Llamada al método del controlador
        ResponseEntity<?> responseEntity = autorController.put(1L, autorActualizado);

        // Comprobación del resultado
        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals("NuevoNombre", autor.getNombre());
        assertEquals("NuevaNacionalidad", autor.getNacionalidad());
        verify(autorRepository).save(autor);
    }

    @Test
    public void testPutNonExistingAutor() {
        // Simulación de un autor inexistente en el repositorio
        when(autorRepository.findById(2L)).thenReturn(Optional.empty());

        Autor autorActualizado = new Autor();
        autorActualizado.setNombre("NuevoNombre");
        autorActualizado.setFechaNacimiento(new Date());
        autorActualizado.setNacionalidad("NuevaNacionalidad");

        // Verificación de que se lanza la excepción apropiada cuando el autor no existe
        BibliotecaNotFoundException exception = assertThrows(BibliotecaNotFoundException.class, () -> autorController.put(2L, autorActualizado));
        assertEquals("Autor not found with id: 2", exception.getMessage());
    }

    */
   /* @Test
    public void testGetAutores() {
        // Datos de ejemplo
        Autor autor1 = new Autor();
        autor1.setId(1L);
        autor1.setNombre("Nombre1");
        autor1.setFechaNacimiento(new Date());
        autor1.setNacionalidad("Nacionalidad1");

        Autor autor2 = new Autor();
        autor2.setId(2L);
        autor2.setNombre("Nombre2");
        autor2.setFechaNacimiento(new Date());
        autor2.setNacionalidad("Nacionalidad2");

        List<Autor> listaAutores = new ArrayList<>();
        listaAutores.add(autor1);
        listaAutores.add(autor2);

        // Configuración del Mock del repositorio para devolver una página de autores
        Page<Autor> page = new PageImpl<>(listaAutores);
        when(autorRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // Llamada al método del controlador
        Page<AutorDto> resultado = autorController.getAutores(null, null, null, "asc", 0, 10);

        // Comprobación del resultado
        assertEquals(listaAutores.size(), resultado.getContent().size());
        // Aquí deberías seguir agregando más aserciones según tu implementación real
    }

    */
    /*@Test
    public void testGetAutoresFiltros() {
        // Datos de ejemplo
        Autor autor1 = new Autor();
        autor1.setId(1L);
        autor1.setNombre("Nombre1");
        autor1.setFechaNacimiento(new Date());
        autor1.setNacionalidad("Nacionalidad1");

        Autor autor2 = new Autor();
        autor2.setId(2L);
        autor2.setNombre("Nombre2");
        autor2.setFechaNacimiento(new Date());
        autor2.setNacionalidad("Nacionalidad2");

        List<Autor> listaAutores = new ArrayList<>();
        listaAutores.add(autor1);
        listaAutores.add(autor2);

        // Configuración del Mock del repositorio para devolver una página de autores
        Page<Autor> page = new PageImpl<>(listaAutores);
        when(autorRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // Llamada al método del controlador
        Page<AutorDto> resultado = autorController.getAutores(null, null, null, "asc", 0, 10);

        // Comprobación del resultado
        assertEquals(listaAutores.size(), resultado.getContent().size());
        // Aquí deberías seguir agregando más aserciones según tu implementación real
    }

     */


}

