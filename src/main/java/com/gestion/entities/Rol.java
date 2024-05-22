package com.gestion.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreRol;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "rol-permisos", joinColumns = {
            @JoinColumn(name = "rol_id", nullable = true) }, inverseJoinColumns = {
            @JoinColumn(name = "permisos_id", nullable = true) })
    private Set<Permisos> permisos;


    public void setId(Long id) {
        this.id = id;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
}
