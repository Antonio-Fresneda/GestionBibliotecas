package com.gestion.repository;


import com.gestion.entities.Autor;
import com.gestion.entities.Genero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GeneroRepository extends JpaRepository<Genero, Long> {
    @Query("SELECT g FROM Genero g WHERE g.nombre = ?1")
    List<Genero> findAllByNombre(String nombre);

    @Query("SELECT g FROM Genero g WHERE g.descripcion =?1")
    List<Genero> findAllByDescripcion(String descripcion);

    @Query("SELECT g FROM Genero g WHERE g.edadRecomendada = ?1")
    List<Genero> findAllByEdadRecomendada(String edadRecomendada);

    @Query("SELECT g FROM Genero g WHERE g.urlWikipedia = ?1")
    List<Genero> findAllByUrlWikipedia(String urlWikipedia);

    /*@Query(value = "SELECT * FROM Autor ORDER BY " +
            "CASE " +
            "WHEN :orderBy = 'nombre' THEN nombre " +
            "WHEN :orderBy = 'descripcion' THEN descripcion " +
            "WHEN :orderBy = 'edadRecomendada' THEN edad_recomendada " +
            "WHEN :orderBy = 'urlWikipedia' THEN url_wikipedia " +
            "ELSE id END :orderDirection", nativeQuery = true)
    Page<Autor> findAllGeneroOrderedBy(@Param("orderBy") String orderBy, @Param("orderDirection") String orderDirection, Pageable pageable);


     */

    @Query(value = "SELECT * FROM Genero ORDER BY " +
            "CASE " +
            "WHEN :orderBy = 'nombre' THEN nombre " +
            "WHEN :orderBy = 'descripcion' THEN descripcion " +
            "WHEN :orderBy = 'edadRecomendada' THEN edad_recomendada " +
            "WHEN :orderBy = 'urlWikipedia' THEN url_wikipedia " +
            "ELSE id END ASC", nativeQuery = true)
    Page<Genero> findAllOrderedBy(@Param("orderBy") String orderBy, Pageable pageable);


    /*@Query(value = "SELECT * FROM Genero ORDER BY " +
            "CASE " +
            "WHEN :orderBy = 'nombre' THEN nombre " +
            "WHEN :orderBy = 'descripcion' THEN descripcion " +
            "WHEN :orderBy = 'edadRecomendada' THEN edad_recomendada " +
            "WHEN :orderBy = 'urlWikipedia' THEN url_wikipedia " +
            "ELSE id END :orderDirection, nombre", nativeQuery = true)
    Page<Genero> findAllOrderedBy(@Param("orderBy") String orderBy, @Param("orderDirection") String orderDirection, Pageable pageable);


     */
}
