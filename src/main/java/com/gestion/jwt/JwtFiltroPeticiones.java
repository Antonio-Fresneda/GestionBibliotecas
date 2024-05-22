package com.gestion.jwt;

import java.io.IOException;

import com.gestion.service.DetalleUsuarioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtFiltroPeticiones extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtFiltroPeticiones.class);

	@Autowired
	private DetalleUsuarioImpl detalleUsuario;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String token = getToken(request);
			if (token != null && JwtProvider.validarTokenJWT(token)) {
				String nombreUsuario = JwtProvider.getEmail(token);
				if (nombreUsuario != null) {
					UserDetails userDetail = detalleUsuario.loadUserByUsername(nombreUsuario);
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetail, null,
							userDetail.getAuthorities());

					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		} catch (Exception e) {
			logger.error("Error al procesar el token JWT", e);
		}

		filterChain.doFilter(request, response);
	}

	private String getToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			return header.replace("Bearer ", "");
		}
		return null;
	}
}
