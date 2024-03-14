package com.gestion.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import jakarta.persistence.Id;


@Entity
@Data
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String título;
    private int anoPublicacion;
    private String isbn;

    //@ManyToOne
    //@JoinColumn(name = "autor_id")
    //private Autor autor;

    //@ManyToMany(mappedBy = "libros")
    //private List<Género> géneros;

}
