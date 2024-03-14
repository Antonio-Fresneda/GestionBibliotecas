package com.gestion.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Data
@Table(name = "libro")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private int anoPublicacion;

    private String isbn;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibroGenero> generos;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL)
    private List<BibliotecaLibro> bibliotecaLibros;

}
