package com.udipsai.backend.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticaciÃ³n JWT
 * Intercepta las peticiones y valida el token JWT en el header Authorization
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Obtener el header de autorizaciÃ³n
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Si no hay header o no empieza con "Bearer", continuar sin autenticar
        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token JWT
        String jwt = authHeader.split(" ")[1].trim();

        // Validar el token
        if (!this.jwtUtil.isValid(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer la cÃ©dula del usuario y cargar sus detalles
        String cedula = this.jwtUtil.getCedula(jwt);
        User user = (User) this.userDetailsService.loadUserByUsername(cedula);

        // Crear el token de autenticaciÃ³n y establecerlo en el contexto de seguridad
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(
                user.getUsername(), 
                user.getPassword(), 
                user.getAuthorities()
            );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
