package com.gestion.service;


import com.gestion.dto.UsuarioDto;
import com.gestion.dto.UsuarioLoginDto;
import com.gestion.entities.Permisos;
import com.gestion.entities.Rol;
import com.gestion.entities.Usuario;
import com.gestion.jwt.JwtProvider;
import com.gestion.repository.RolRepository;
import com.gestion.repository.UsuarioRepository;
import com.gestion.service.mapper.UsuarioMapper;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioMapper usuarioMapper;


	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private RolRepository rolRepository;

	@Override
	public UsuarioDto crear(UsuarioDto usuarioDTO) throws Exception  {

		Usuario usuario = usuarioMapper.toUsuario(usuarioDTO);
		usuario.setClave(passwordEncoder.encode(usuarioDTO.getClave()));
		Rol rol = rolRepository.findByNombreRol(usuarioDTO.getRol()).orElseThrow(()-> new Exception("No existe el rol en la base de datos, inserte primero"));
		usuario.setRol(rol);
		usuario = usuarioRepository.save(usuario);
		return usuarioMapper.toUsuarioDTO(usuario);

	}
	@Override
	public UsuarioDto login(UsuarioLoginDto usuarioLoginDTO) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(usuarioLoginDTO.getEmail(), usuarioLoginDTO.getClave()));
		SecurityContextHolder.getContext().setAuthentication(auth);

		String token = JwtProvider.generarTokenJWT(usuarioLoginDTO.getEmail());

		Usuario usuario = usuarioRepository.findByEmail(usuarioLoginDTO.getEmail()).orElse(null);
		UsuarioDto usuarioDTO = usuarioMapper.toUsuarioDTO(usuario);
		usuarioDTO.setToken(token);

		Set<Permisos> permisos = usuario.getRol().getPermisos();
		List<String> permisosUsuario = permisos.stream().map(Permisos::getNombre).collect(Collectors.toList());
		String permisosUsuarioString = String.join(", ", permisosUsuario);
		usuarioDTO.setPermisos(permisosUsuarioString);

		return usuarioDTO;
	}


	/*@Override
	public UsuarioDto login(UsuarioLoginDto usuarioLoginDTO) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(usuarioLoginDTO.getEmail(), usuarioLoginDTO.getClave()));
		SecurityContextHolder.getContext().setAuthentication(auth);

		String token = JwtProvider.generarTokenJWT(usuarioLoginDTO.getEmail());

		Usuario usuario = usuarioRepository.findByEmail(usuarioLoginDTO.getEmail()).orElse(null);

		UsuarioDto usuarioDTO = usuarioMapper.toUsuarioDTO(usuario);

		usuarioDTO.setToken(token);
		return usuarioDTO;
	}

	 */

}
