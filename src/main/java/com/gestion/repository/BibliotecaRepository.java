package com.gestion.repository;

import com.gestion.entities.Biblioteca;
import org.springframework.cglib.core.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BibliotecaRepository extends JpaRepository<Biblioteca, Long> {

    //@Query("SELECT b FROM Bliblioteca b WHERE b.code = ?1")
    //public Block findByNombre(String nombre);


}
