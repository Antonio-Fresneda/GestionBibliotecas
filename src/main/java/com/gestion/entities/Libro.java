package com.gestion.entities;

import jakarta.persistence.*;
import lombok.Data;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "libro-genero", joinColumns = {
            @JoinColumn(name = "libro_id", nullable = true) }, inverseJoinColumns = {
            @JoinColumn(name = "genero_id", nullable = true) })
    private Set<Genero> generos;


    public Libro(long id, String titulo, int anoPublicacion, String isbn) {
        this.id = id;
        this.titulo = titulo;
        this.anoPublicacion = anoPublicacion;
        this.isbn = isbn;
    }

    public Libro() {

    }


}
