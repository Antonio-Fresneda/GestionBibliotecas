package com.gestion.repository;

import com.gestion.entities.Autor;
import com.gestion.entities.Biblioteca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {
}
