package com.gestion.service.mapper;

import com.gestion.dto.UsuarioDto;
import com.gestion.entities.NombreRol;
import com.gestion.entities.Rol;
import com.gestion.entities.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-07T12:01:39+0200",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public UsuarioDto toUsuarioDTO(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UsuarioDto usuarioDto = new UsuarioDto();

        usuarioDto.setRol( usuarioRolNombreRol( usuario ) );
        usuarioDto.setId( usuario.getId() );
        usuarioDto.setEmail( usuario.getEmail() );
        usuarioDto.setClave( usuario.getClave() );
        usuarioDto.setNombre( usuario.getNombre() );
        usuarioDto.setApellidos( usuario.getApellidos() );
        usuarioDto.setTelefono( usuario.getTelefono() );
        usuarioDto.setFechaNacimiento( usuario.getFechaNacimiento() );

        return usuarioDto;
    }

    @Override
    public Usuario toUsuario(UsuarioDto usuarioDTO) {
        if ( usuarioDTO == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setId( usuarioDTO.getId() );
        usuario.setClave( usuarioDTO.getClave() );
        usuario.setRol( nombreRolToRol( usuarioDTO.getRol() ) );
        usuario.setNombre( usuarioDTO.getNombre() );
        usuario.setApellidos( usuarioDTO.getApellidos() );
        usuario.setTelefono( usuarioDTO.getTelefono() );
        usuario.setEmail( usuarioDTO.getEmail() );
        usuario.setFechaNacimiento( usuarioDTO.getFechaNacimiento() );

        return usuario;
    }

    private NombreRol usuarioRolNombreRol(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }
        Rol rol = usuario.getRol();
        if ( rol == null ) {
            return null;
        }
        NombreRol nombreRol = rol.getNombreRol();
        if ( nombreRol == null ) {
            return null;
        }
        return nombreRol;
    }

    protected Rol nombreRolToRol(NombreRol nombreRol) {
        if ( nombreRol == null ) {
            return null;
        }

        Rol rol = new Rol();

        rol.setNombreRol( nombreRol );

        return rol;
    }
}
