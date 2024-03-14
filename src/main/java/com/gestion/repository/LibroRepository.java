package com.gestion.repository;

import com.gestion.entities.Biblioteca;
import com.gestion.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {
}
