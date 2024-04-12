package com.gestion.entities;


import jakarta.persistence.*;
import lombok.Data;
import org.ietf.jgss.GSSName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    //@OneToMany(fetch = FetchType.LAZY, mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<LibroGenero> generos = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "genero_id")
    private Genero genero;

    public Libro(long id, String titulo, int anoPublicacion, String isbn) {
        this.id = id;
        this.titulo = titulo;
        this.anoPublicacion = anoPublicacion;
        this.isbn = isbn;
    }

    public Libro() {

    }


}
