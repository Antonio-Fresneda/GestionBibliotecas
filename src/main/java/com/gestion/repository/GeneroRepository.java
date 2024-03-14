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
    List<Genero> findAllByNombre( String nombre);

    @Query("SELECT g FROM Genero g WHERE g.descripcion =?1")
    List<Genero> findAllByDescripcion( String descripcion);

    @Query("SELECT g FROM Genero g WHERE g.edadRecomendada = ?1")
    List<Genero> findAllByEdadRecomendada( String edadRecomendada);

    @Query("SELECT g FROM Genero g WHERE g.urlWikipedia = ?1")
    List<Genero> findAllByUrlWikipedia(String urlWikipedia);

    @Query("SELECT a FROM Genero a ORDER BY " +
            "CASE WHEN :orderBy = 'nombre' THEN a.nombre " +
            "WHEN :orderBy = 'descripcion' THEN a.descripcion " +
            "WHEN :orderBy = 'edadRecomendada' THEN a.edadRecomendada " +
            "WHEN :orderBy = 'urlWikipedia' THEN a.urlWikipedia " +
            "ELSE a.id END ASC")
    Page<Genero> findAllGeneroOrderedBy(@Param("orderBy") String orderBy, Pageable pageable);
}
