package com.gestion.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NombreRol nombreRol;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }







}
