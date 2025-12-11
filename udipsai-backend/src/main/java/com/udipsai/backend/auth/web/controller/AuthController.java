package com.udipsai.backend.auth.web.controller;

import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;
import com.udipsai.backend.common.service.UsuarioService;
import com.udipsai.backend.auth.service.dto.LoginDto;
import com.udipsai.backend.common.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(loginDto.getCedula(),
                    loginDto.getContrasenia());
            logger.info("Login de usuario con cedula: " + loginDto.getCedula());
            Authentication authentication = this.authenticationManager.authenticate(login);
            Optional<UsuarioEntity> optionalUsuario = usuarioService.findByCedula(loginDto.getCedula());
            if (optionalUsuario.isPresent()) {
                UsuarioEntity usuario = optionalUsuario.get();
                String jwt = this.jwtUtil.create(loginDto.getCedula());
                logger.info("200 OK: Usuario autenticado");
                return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwt).body(usuario);
            } else {
                logger.error("401 UNAUTHORIZED: Credenciales invÃ¡lidas");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
            }
        } catch (AuthenticationException e) {
            logger.error("401 UNAUTHORIZED: Credenciales invÃ¡lidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales invÃ¡lidas");
        }
    }
}

