package com.udipsai.backend.common.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Utilidad para manejo de JWT (JSON Web Tokens)
 * Compartido por todos los mÃ³dulos del backend
 */
@Component
public class JwtUtil {

    // TODO: Mover esto a variables de entorno en producciÃ³n
    @Value("${jwt.secret:Jeremy_25312}")
    private String secretKey;
    
    @Value("${jwt.expiration.days:15}")
    private int expirationDays;
    
    @Value("${jwt.issuer:UCACUE_UDIPSAI}")
    private String issuer;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    /**
     * Crea un token JWT para el usuario identificado por cÃ©dula
     */
    public String create(String cedula) {
        return JWT.create()
                .withSubject(cedula)
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(expirationDays)))
                .sign(getAlgorithm());
    }

    /**
     * Valida si un token JWT es vÃ¡lido
     */
    public boolean isValid(String jwt) {
        try {
            JWT.require(getAlgorithm()).build().verify(jwt);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * Extrae la cÃ©dula del usuario desde el token JWT
     */
    public String getCedula(String jwt) {
        return JWT.require(getAlgorithm()).build().verify(jwt).getSubject();
    }
}
