package com.gestion.repository;

import com.gestion.entities.Autor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long>, JpaSpecificationExecutor<Autor> {

    Page<Autor> findAllByNombre(String nombre, Pageable pageable);

    Page<Autor> findAllByFechaNacimiento(Date fechaNacimiento, Pageable pageable);

    Page<Autor> findAllByNacionalidad(String nacionalidad, Pageable pageable);

    Page<Autor> findAllByNombreAndFechaNacimiento(String nombre, Date fechaNacimiento, Pageable pageable);

    Page<Autor> findAllByNombreAndNacionalidad(String nombre, String nacionalidad, Pageable pageable);

    Page<Autor> findAllByFechaNacimientoAndNacionalidad(Date fechaNacimiento, String nacionalidad, Pageable pageable);

    Page<Autor> findAllByNombreAndFechaNacimientoAndNacionalidad(String nombre, Date fechaNacimiento, String nacionalidad, Pageable pageable);

    /*@Query(value = "SELECT * FROM Autor ORDER BY " +
            "CASE " +
            "WHEN :orderBy = 'nombre' THEN nombre " +
            "WHEN :orderBy = 'fechaNacimiento' THEN fecha_nacimiento " +
            "WHEN :orderBy = 'nacionalidad' THEN nacionalidad " +
            "ELSE id END ASC", nativeQuery = true)
    Page<Autor> findAllOrderedBy(@Param("orderBy") String orderBy, Pageable pageable);

     */

    /*@Query(value = "SELECT * FROM Autor ORDER BY " +
            "CASE " +
            "WHEN :orderBy = 'nombre' THEN nombre " +
            "WHEN :orderBy = 'fechaNacimiento' THEN fecha_nacimiento " +
            "WHEN :orderBy = 'nacionalidad' THEN nacionalidad " +
            "ELSE id END :orderDirection", nativeQuery = true)
    Page<Autor> findAllOrderedBy(@Param("orderBy") String orderBy, @Param("orderDirection") String orderDirection, Pageable pageable);
     */
    /*@Query(value = "SELECT * FROM Autor ORDER BY " +
            "CASE " +
            "WHEN :orderBy = 'nombre' THEN nombre " +
            "WHEN :orderBy = 'fechaNacimiento' THEN fecha_nacimiento " +
            "WHEN :orderBy = 'nacionalidad' THEN nacionalidad " +
            "ELSE id END ASC", nativeQuery = true)
    Page<Autor> findAllOrderedBy(@Param("orderBy") String orderBy, Pageable pageable);

     */
    @Query(value = "SELECT * FROM Autor ORDER BY " +
            "CASE " +
            "WHEN :orderBy = 'nombre' THEN nombre " +
            "WHEN :orderBy = 'fechaNacimiento' THEN fecha_nacimiento " +
            "WHEN :orderBy = 'nacionalidad' THEN nacionalidad " +
            "ELSE id END :direction", nativeQuery = true)
    Page<Autor> findAllOrderedBy(@Param("orderBy") String orderBy, @Param("direction") String direction, Pageable pageable);


    @Transactional
    @Modifying
    @Query("DELETE FROM LibroGenero lg WHERE lg.libro.autor.id = :autorId")
    void deleteLibroGeneroByAutorId(@Param("autorId") long autorId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Libro l WHERE l.autor.id = :autorId")
    void deleteLibrosByAutorId(@Param("autorId") long autorId);

}

