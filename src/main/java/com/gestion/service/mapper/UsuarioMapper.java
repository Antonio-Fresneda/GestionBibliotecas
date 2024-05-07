package com.gestion.service.mapper;

import com.gestion.dto.UsuarioDto;
import com.gestion.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;




@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "rol.nombreRol", target = "rol")
    public UsuarioDto toUsuarioDTO(Usuario usuario);

    @Mapping(target = "authorities", ignore = true)
    public Usuario toUsuario(UsuarioDto usuarioDTO);

}
