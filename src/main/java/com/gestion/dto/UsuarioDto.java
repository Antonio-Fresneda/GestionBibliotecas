package com.gestion.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gestion.entities.NombreRol;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class UsuarioDto {

    private Long id;

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String clave;

    @NotNull
    @NotBlank
    private String nombre;

    @NotNull
    @NotBlank
    private String apellidos;

    private String telefono;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date fechaNacimiento;

    @NotNull
    private NombreRol rol;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String token;
}
