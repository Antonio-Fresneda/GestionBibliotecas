package com.gestion.repository;

import com.gestion.entities.Autor;
import com.gestion.entities.Biblioteca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    @Query("SELECT a FROM Autor a WHERE a.nombre = ?1")
    List<Autor> findAllByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento = ?1")
    List<Autor> findAllByFechaNacimiento(Date fechaNacimiento);

    @Query("SELECT a FROM Autor a WHERE a.nacionalidad = ?1")
    List<Autor> findAllByNacionalidad(String nacionalidad);
}
