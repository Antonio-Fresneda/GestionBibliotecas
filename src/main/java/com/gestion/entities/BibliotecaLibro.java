package com.gestion.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "biblioteca_libro")
public class BibliotecaLibro {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "libro_id")
    private Libro libro;


    @ManyToOne(optional = true)
    @JoinColumn(name = "biblioteca_id")
    private Biblioteca biblioteca;

}
