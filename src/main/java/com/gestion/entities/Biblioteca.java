package com.gestion.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "biblioteca")
public class Biblioteca {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private String direccion;

	private String telefono;
	private String email;
	private String sitioWeb;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "biblioteca_libro", joinColumns = {
			@JoinColumn(name = "biblioteca_id", nullable = true) }, inverseJoinColumns = {
			@JoinColumn(name = "libro_id", nullable = true) })
	private Set<Libro> libros;






}
