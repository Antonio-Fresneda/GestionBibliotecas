package com.gestion.repository;

import com.gestion.entities.BibliotecaLibro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliotecaLibroRepository extends JpaRepository<BibliotecaLibro, Long> {
}
