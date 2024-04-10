package com.gestion.repository;

import com.gestion.entities.*;
import com.gestion.entities.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    @Query("SELECT a FROM Libro a WHERE a.titulo = ?1")
    Libro findByTitulo(String titulo);

    @Query("SELECT a FROM Libro a WHERE a.anoPublicacion = ?1")
    List<Libro> findAllByAnoPublicacion(int anoPublicacion);

    @Query("SELECT a FROM Libro a WHERE a.isbn = ?1")
    List<Libro> findAllByIsbn(String isbn);

    @Query("SELECT a FROM Libro a WHERE a.autor = ?1")
    List<Libro> findAllByAutor(Autor autor);

   @Query(value = "SELECT * FROM Libro ORDER BY " +
           "CASE " +
           "WHEN :orderBy = 'titulo' THEN titulo " +
           "WHEN :orderBy = 'anoPublicacion' THEN ano_publicacion " +
           "WHEN :orderBy = 'isbn' THEN isbn " +
           "ELSE id END ASC", nativeQuery = true)
   Page<Libro> findAllOrderedBy(@Param("orderBy") String orderBy, Pageable pageable);

    Page<Libro> findAllByTituloContaining(String titulo, Pageable pageable);

    Page<Libro> findAllByAnoPublicacion(Integer anoPublicacion, Pageable pageable);

    Page<Libro> findAllByIsbnContaining(String isbn, Pageable pageable);


    Page<Libro> findAllByTituloContainingAndAnoPublicacion(String titulo, Integer anoPublicacion, Pageable pageable);

    Page<Libro> findAllByTituloContainingAndIsbnContaining(String titulo, String isbn, Pageable pageable);


    Page<Libro> findAllByAnoPublicacionAndIsbnContaining(Integer anoPublicacion, String isbn, Pageable pageable);

    Page<Libro> findAllByTituloContainingAndAnoPublicacionAndIsbnContaining(String titulo, Integer anoPublicacion, String isbn, Pageable pageable);




}



