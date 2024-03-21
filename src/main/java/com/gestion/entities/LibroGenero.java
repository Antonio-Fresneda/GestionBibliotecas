package com.gestion.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "libro_genero")
public class LibroGenero {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    @JoinColumn(name = "genero_id")
    private long generoId;
    @Transient
    private String generoNombre;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,targetEntity = Libro.class)
    private Libro libro;


}
