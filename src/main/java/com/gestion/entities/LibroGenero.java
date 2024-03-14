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
    private long generoId;
    @Transient
    private String generoNombre;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,targetEntity = Libro.class)
    @JoinColumn(name = "customerId", nullable = true)
    private Libro libro;


}
