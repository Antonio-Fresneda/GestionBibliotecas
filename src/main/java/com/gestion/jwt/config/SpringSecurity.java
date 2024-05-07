package com.gestion.jwt.config;

import com.gestion.jwt.JwtAccesoDenegadoError;
import com.gestion.jwt.JwtAutenticacionError;
import com.gestion.jwt.JwtFiltroPeticiones;
import com.gestion.service.DetalleUsuarioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@ComponentScan(basePackages = {"com.gestion.service", "com.gestion.service.mapper"})
@EnableWebSecurity
public class SpringSecurity  {

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
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeHttpRequests(authConfig -> {
                    authConfig.requestMatchers("/usuario/login").permitAll();
                    authConfig.requestMatchers("/usuario/crear").permitAll();
                    authConfig.anyRequest().authenticated();
                }).exceptionHandling().authenticationEntryPoint(jwtAutenticacionError).accessDeniedHandler(jwtAccesoDenegadoError).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.userDetailsService(detalleUsuarioImpl);
        http.addFilterBefore(jwtFiltroPeticiones(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuario/login").permitAll()
                        .requestMatchers("/usuario/crear").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .userDetailsService(detalleUsuarioImpl)
                .addFilterBefore( jwtFiltroPeticiones(),  UsernamePasswordAuthenticationFilter.class)
                .build();
    }
     */
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/usuario/login").permitAll()
                        .requestMatchers("/usuario/crear").permitAll()
                        .anyRequest().permitAll()
                ).sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .userDetailsService(detalleUsuarioImpl)
                .addFilterBefore( jwtFiltroPeticiones(),  UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
   /* @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
    */
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authz) ->
                        authz
                                .requestMatchers("usuario/crear").permitAll()
                                .requestMatchers("usuario/login").permitAll()
                                .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin((form) ->
                        form.
                                loginPage("/usuario/login").permitAll()
                                .loginProcessingUrl("/usuario/login")
                                .defaultSuccessUrl("/")
                                .permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

     */






}





