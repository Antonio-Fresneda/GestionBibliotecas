package com.gestion.dto;

import lombok.Data;

@Data
public class GeneroDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer edadRecomendada;
    private String urlWikipedia;
}
