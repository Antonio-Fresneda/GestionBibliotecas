package com.gestion.repository;

import com.gestion.entities.NombreRol;
import com.gestion.entities.Rol;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface RolRepository extends CrudRepository<Rol, Long> {
    Optional<Rol> findByNombreRol(NombreRol nombreRol);
}
