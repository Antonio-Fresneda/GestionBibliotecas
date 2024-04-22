package com.gestion.dto;

import com.gestion.entities.Libro;
import lombok.Data;

import java.util.Set;

@Data
public class BibliotecaDto {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String sitioWeb;

}
