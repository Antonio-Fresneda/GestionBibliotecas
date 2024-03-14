package com.gestion.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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

	@OneToMany(mappedBy = "biblioteca", cascade = CascadeType.ALL)
	private List<BibliotecaLibro> bibliotecaLibros;
}
