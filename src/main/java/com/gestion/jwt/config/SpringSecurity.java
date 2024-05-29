package com.gestion.jwt.config;

import com.gestion.jwt.JwtAccesoDenegadoError;
import com.gestion.jwt.JwtAutenticacionError;
import com.gestion.jwt.JwtFiltroPeticiones;
import com.gestion.service.DetalleUsuarioImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurity {

    @Autowired
    private DetalleUsuarioImpl detalleUsuarioImpl;

    @Autowired
    private JwtAutenticacionError jwtAutenticacionError;

    @Autowired
    private JwtAccesoDenegadoError jwtAccesoDenegadoError;

    @Bean
    JwtFiltroPeticiones jwtFiltroPeticiones() {
        return new JwtFiltroPeticiones();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authConfig -> {
                    authConfig.requestMatchers("/usuario/login").permitAll();
                    authConfig.requestMatchers("/usuario/registrar").permitAll();
                    authConfig.anyRequest().authenticated();
                }).exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAutenticacionError)
                        .accessDeniedHandler(jwtAccesoDenegadoError))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.userDetailsService(detalleUsuarioImpl);
        http.addFilterAfter(jwtFiltroPeticiones(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }




}





