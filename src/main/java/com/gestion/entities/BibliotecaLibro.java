package com.gestion.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "biblioteca_libro")
public class BibliotecaLibro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "biblioteca_id", nullable = false)
    private Biblioteca biblioteca;

    @ManyToOne
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;
}
