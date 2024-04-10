package com.gestion.dto;

import lombok.Data;

import java.util.List;

@Data
public class LibroDto {
    private Long id;
    private String titulo;
    private int anoPublicacion;
    private String isbn;

}