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

    Page<Biblioteca> findAllByNombreContainingAndDireccionContainingAndTelefonoContainingAndSitioWebContaining(String nombre, String direccion, String telefono, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndDireccionContainingAndTelefonoContaining(String nombre, String direccion, String telefono, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndDireccionContainingAndEmailContainingAndSitioWebContaining(String nombre, String direccion, String email, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndDireccionContainingAndEmailContaining(String nombre, String direccion, String email, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndDireccionContainingAndSitioWebContaining(String nombre, String direccion, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndDireccionContaining(String nombre, String direccion, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndTelefonoContainingAndEmailContainingAndSitioWebContaining(String nombre, String telefono, String email, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndTelefonoContainingAndEmailContaining(String nombre, String telefono, String email, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndTelefonoContainingAndSitioWebContaining(String nombre, String telefono, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndTelefonoContaining(String nombre, String telefono, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndEmailContainingAndSitioWebContaining(String nombre, String email, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndEmailContaining(String nombre, String email, Pageable pageable);

    Page<Biblioteca> findAllByNombreContainingAndSitioWebContaining(String nombre, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByDireccionContainingAndTelefonoContainingAndEmailContainingAndSitioWebContaining(String direccion, String telefono, String email, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByDireccionContainingAndTelefonoContainingAndEmailContaining(String direccion, String telefono, String email, Pageable pageable);

    Page<Biblioteca> findAllByDireccionContainingAndTelefonoContainingAndSitioWebContaining(String direccion, String telefono, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByDireccionContainingAndTelefonoContaining(String direccion, String telefono, Pageable pageable);

    Page<Biblioteca> findAllByDireccionContainingAndEmailContainingAndSitioWebContaining(String direccion, String email, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByDireccionContainingAndEmailContaining(String direccion, String email, Pageable pageable);

    Page<Biblioteca> findAllByDireccionContainingAndSitioWebContaining(String direccion, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByTelefonoContainingAndEmailContainingAndSitioWebContaining(String telefono, String email, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByTelefonoContainingAndEmailContaining(String telefono, String email, Pageable pageable);

    Page<Biblioteca> findAllByTelefonoContainingAndSitioWebContaining(String telefono, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByEmailContainingAndSitioWebContaining(String email, String sitioWeb, Pageable pageable);

    Page<Biblioteca> findAllByNombreContaining(String nombre, Pageable pageable);

    Page<Biblioteca> findAllByDireccionContaining(String direccion, Pageable pageable);

    Page<Biblioteca> findAllByTelefonoContaining(String telefono, Pageable pageable);

    Page<Biblioteca> findAllByEmailContaining(String email, Pageable pageable);

    Page<Biblioteca> findAllBySitioWebContaining(String sitioWeb, Pageable pageable);

}
