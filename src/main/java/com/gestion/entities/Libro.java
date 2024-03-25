package com.gestion.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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

    @ManyToOne(cascade = CascadeType.ALL,optional = true)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibroGenero> generos = new ArrayList<>();

    @OneToMany(mappedBy = "id")
    private List<BibliotecaLibro> libroBibliotecas = new ArrayList<>();

    public Libro(long id, String titulo, int anoPublicacion, String isbn) {
        this.id = id;
        this.titulo = titulo;
        this.anoPublicacion = anoPublicacion;
        this.isbn = isbn;
    }

    public Libro() {

    }

    public void addGenero(Genero genero) {
        LibroGenero libroGenero = new LibroGenero();
        libroGenero.setGenero(genero);
        libroGenero.setLibro(this);
        generos.add(libroGenero);
    }
}
