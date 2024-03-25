package com.gestion.repository;

import com.gestion.entities.Autor;
import com.gestion.entities.Biblioteca;
import com.gestion.entities.Genero;
import com.gestion.entities.Libro;
import org.springframework.cglib.core.Block;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BibliotecaRepository extends JpaRepository<Biblioteca, Long> {

    @Query("SELECT a FROM Biblioteca a WHERE a.nombre = ?1")
    List<Biblioteca> findAllByNombre(String nombre);

    @Query("SELECT a FROM Biblioteca a WHERE a.direccion = ?1")
    List<Biblioteca> findAllByDireccion(String direccion);

    @Query("SELECT a FROM Biblioteca a WHERE a.telefono = ?1")
    List<Biblioteca> findAllByTelefono(String telefono);

    @Query("SELECT a FROM Biblioteca a WHERE a.email = ?1")
    List<Biblioteca> findAllByEmail(String email);
    @Query("SELECT a FROM Biblioteca a WHERE a.sitioWeb = ?1")
    List<Biblioteca> findAllByWeb(String sitioWeb);

    @Query(value = "SELECT * FROM Biblioteca ORDER BY " +
            "CASE " +
            "WHEN :orderBy = 'nombre' THEN nombre " +
            "WHEN :orderBy = 'direccion' THEN direccion " +
            "WHEN :orderBy = 'telefono' THEN telefono " +
            "WHEN :orderBy = 'email' THEN email " +
            "WHEN :orderBy = 'sitioWeb' THEN sitio_web " +
            "ELSE id END ASC", nativeQuery = true)
    Page<Biblioteca> findAllOrderedBy(@Param("orderBy") String orderBy, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndDireccionContainingAndTelefonoContainingAndEmailContainingAndSitioWebContaining(String nombre, String direccion, String telefono, String email, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndDireccionContainingAndTelefonoContainingAndEmailContaining(String nombre, String direccion, String telefono, String email, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndDireccionContainingAndTelefonoContaining(String nombre, String direccion, String telefono, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndDireccionContaining(String nombre, String direccion, Pageable pageable);

    Page<Biblioteca> findAllByNombreContaining(String nombre, Pageable pageable);



}
