package com.gestion.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
//@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private  Date fechaNacimiento;
    private String nacionalidad;

    //@OneToMany(mappedBy = "autor")
    //private List<Libro> libros;
}
