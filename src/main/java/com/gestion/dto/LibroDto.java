package com.gestion.dto;

import lombok.Data;

@Data
public class LibroDto {
    private Long id;
    private String titulo;
    private int anoPublicacion;
    private String isbn;
    private Long autorId;

}