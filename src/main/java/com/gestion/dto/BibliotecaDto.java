package com.gestion.dto;

import lombok.Data;

@Data
public class BibliotecaDto {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String sitioWeb;

}
