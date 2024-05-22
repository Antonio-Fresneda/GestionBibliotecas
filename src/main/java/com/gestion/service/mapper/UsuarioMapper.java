package com.gestion.service.mapper;

import com.gestion.dto.UsuarioDto;
import com.gestion.entities.Rol;
import com.gestion.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "rol.nombreRol", target = "rol")
    public UsuarioDto toUsuarioDTO(Usuario usuario);

    @Mapping(target = "authorities", ignore = true)
    @Mapping(source = "rol", target = "rol", qualifiedByName = "stringToRol")
    public Usuario toUsuario(UsuarioDto usuarioDTO);

    @Named("stringToRol")
    default Rol stringToRol(String nombreRol) {
        Rol rol = new Rol();
        rol.setNombreRol(nombreRol);
        return rol;
    }
}
