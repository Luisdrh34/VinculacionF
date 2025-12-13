package com.udipsai.backend.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * ConfiguraciÃ³n de seguridad unificada para todo el backend
 * Maneja autenticaciÃ³n JWT y autorizaciÃ³n basada en roles
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final LogFilter logFilter;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter, LogFilter logFilter) {
        this.jwtFilter = jwtFilter;
        this.logFilter = logFilter;
    }

    // URLs que no requieren autenticaciÃ³n
    private static final String[] WHITE_LIST_URL = {
            "/api/hello/**",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/error"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (no necesario en API REST con JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // Agregar filtros personalizados
                .addFilterBefore(logFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Configurar sesiones como STATELESS
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))

                // Habilitar CORS
                .cors(cors -> {
                })

                // Configurar autorizaciÃ³n de endpoints
                .authorizeHttpRequests(auth -> auth
                        // ========== Endpoints PÃºblicos ==========
                        .requestMatchers(WHITE_LIST_URL).permitAll()

                        // ========== MÃ³dulo de AutenticaciÃ³n ==========
                        .requestMatchers(POST, "/api/auth/login").permitAll()
                        .requestMatchers(POST, "/api/auth/register").permitAll()
                        .requestMatchers(POST, "/api/auth/refresh").permitAll()

                        // ========== MÃ³dulo de Usuarios ==========
                        // Admins
                        .requestMatchers(POST, "/api/admins").permitAll() // Primer registro
                        .requestMatchers(GET, "/api/admins/**").hasAnyRole("ADMIN")
                        .requestMatchers(PUT, "/api/admins/**").hasAnyRole("ADMIN")
                        .requestMatchers(PATCH, "/api/admins/**").hasAnyRole("ADMIN")
                        .requestMatchers(DELETE, "/api/admins/**").hasAnyRole("ADMIN")

                        // Usuarios genéricos
                        .requestMatchers(GET, "/api/usuarios/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(PATCH, "/api/usuarios/cambiarContrasenia/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR", "PASANTES")
                        .requestMatchers(PATCH, "/api/usuarios/habilitar/**", "/api/usuarios/deshabilitar/**",
                                "/api/usuarios/bloquear/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(DELETE, "/api/usuarios/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")

                        // Secretarias
                        .requestMatchers(POST, "/api/secretarias").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(GET, "/api/secretarias/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(PUT, "/api/secretarias/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(PATCH, "/api/secretarias/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(DELETE, "/api/secretarias/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")

                        // Coordinadores
                        .requestMatchers(POST, "/api/coordinadores").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(GET, "/api/coordinadores/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(PUT, "/api/coordinadores/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(PATCH, "/api/coordinadores/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(DELETE, "/api/coordinadores/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")

                        // Profesionales (Doctores)
                        .requestMatchers(POST, "/api/profesionales").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(GET, "/api/profesionales/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(PUT, "/api/profesionales/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(PATCH, "/api/profesionales/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(DELETE, "/api/profesionales/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")

                        // Pasantes
                        .requestMatchers(POST, "/api/pasantes").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(GET, "/api/pasantes/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(PUT, "/api/pasantes/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR", "PASANTES")
                        .requestMatchers(PATCH, "/api/pasantes/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(DELETE, "/api/pasantes/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")

                        // Áreas
                        .requestMatchers("/api/areas/**").permitAll() // Acceso público para listado

                        // Roles
                        .requestMatchers(POST, "/api/roles").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(GET, "/api/roles/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(PATCH, "/api/roles/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(DELETE, "/api/roles/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")

                        // ========== Módulo de Citas ==========
                        .requestMatchers(POST, "/api/citas").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(GET, "/api/citas/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(PUT, "/api/citas/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(DELETE, "/api/citas/**").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")

                        // Pacientes
                        .requestMatchers(POST, "/api/pacientes").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(GET, "/api/pacientes/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA", "DOCTOR")
                        .requestMatchers(PUT, "/api/pacientes/**").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")
                        .requestMatchers(DELETE, "/api/pacientes/**").hasAnyRole("ADMIN", "SECRETARIA", "COORDINADORA")

                        // Cualquier otra peticiÃ³n requiere autenticaciÃ³n
                        .anyRequest().authenticated());

        return http.build();
    }
}
