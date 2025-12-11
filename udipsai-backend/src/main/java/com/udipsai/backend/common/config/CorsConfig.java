package com.udipsai.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * ConfiguraciÃ³n de CORS (Cross-Origin Resource Sharing)
 * Permite peticiones desde el frontend y otros orÃ­genes
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Permitir todos los orÃ­genes (usar patrones especÃ­ficos en producciÃ³n)
        corsConfiguration.addAllowedOriginPattern("*");

        // MÃ©todos HTTP permitidos
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Encabezados permitidos
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));

        // Exponer encabezados especÃ­ficos (importante para JWT)
        corsConfiguration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Permitir credenciales (cookies, headers de autorizaciÃ³n, etc.)
        corsConfiguration.setAllowCredentials(true);

        // Tiempo de cache de configuraciÃ³n CORS
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
