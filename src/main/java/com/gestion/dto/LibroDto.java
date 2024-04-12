package com.gestion.dto;

import org.springframework.lang.Nullable;
import lombok.Data;

import java.util.List;

@Data
public class LibroDto {

    private Long id;
    private String titulo;
    private int anoPublicacion;
    private String isbn;
    private Long autorId;
    private Long generoId;
}
