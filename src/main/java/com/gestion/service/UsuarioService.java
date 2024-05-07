package com.gestion.service;


import com.gestion.dto.UsuarioDto;
import com.gestion.dto.UsuarioLoginDto;

public interface UsuarioService {

	public UsuarioDto login(UsuarioLoginDto usuarioLoginDTO);

	public UsuarioDto crear(UsuarioDto usuarioDTO) throws Exception;
}
