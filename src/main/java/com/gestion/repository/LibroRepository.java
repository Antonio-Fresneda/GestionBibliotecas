package com.gestion.repository;

import com.gestion.entities.Autor;
import com.gestion.entities.Libro;
import com.gestion.entities.Biblioteca;
import com.gestion.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    @Query("SELECT a FROM Libro a WHERE a.titulo = ?1")
    List<Libro> findAllByTitulo(String titulo);

    @Query("SELECT a FROM Libro a WHERE a.anoPublicacion = ?1")
    List<Libro> findAllByAnoPublicacion(int anoPublicacion);

    @Query("SELECT a FROM Libro a WHERE a.isbn = ?1")
    List<Libro> findAllByIsbn(String isbn);

    @Query("SELECT a FROM Libro a WHERE a.autor = ?1")
    List<Libro> findAllByAutor(Autor autor);
}
