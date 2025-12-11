package com.udipsai.backend.common.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro de logging para registrar informaciÃ³n de cada peticiÃ³n HTTP
 * Ãštil para debugging y auditorÃ­a
 */
@Component
public class LogFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Log de informaciÃ³n de la peticiÃ³n
        logger.debug("========== REQUEST DATA ==========");
        logger.debug("Origin IP=[{}]", request.getRemoteAddr());
        logger.debug("URI=[{}]", request.getRequestURI());
        logger.debug("Method=[{}]", request.getMethod());
        logger.debug("Host Header=[{}]", request.getHeader("Host"));
        logger.debug("Server IP=[{}]", request.getLocalAddr());
        logger.debug("Content-Type=[{}]", request.getHeader("Content-Type"));
        logger.debug("User-Agent=[{}]", request.getHeader("User-Agent"));
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
        
        // Log de respuesta
        logger.debug("Response Status=[{}]", response.getStatus());
    }
}
