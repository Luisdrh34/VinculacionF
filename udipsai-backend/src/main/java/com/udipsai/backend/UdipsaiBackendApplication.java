package com.udipsai.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AplicaciÃ³n principal del backend unificado de UDIPSAI
 * Integra los mÃ³dulos de autenticaciÃ³n, usuarios y citas en un solo servicio
 */
@SpringBootApplication
public class UdipsaiBackendApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(UdipsaiBackendApplication.class);

    @org.springframework.beans.factory.annotation.Autowired
    private com.udipsai.backend.usuarios.persistence.repository.UsuarioRepository usuarioRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(UdipsaiBackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info("==============================================");
        logger.info("Sistema UDIPSAI Backend Iniciado Correctamente");

        try {
            String cedula = "1234567890";
            java.util.Optional<com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity> usuarioOpt = usuarioRepository
                    .findByCedula(cedula);
            if (usuarioOpt.isPresent()) {
                com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity usuario = usuarioOpt.get();
                usuario.setContrasenia(passwordEncoder.encode("123456"));
                usuarioRepository.save(usuario);
                logger.info("CONTRASEÑA RESTABLECIDA PARA USUARIO " + cedula + " A '123456'");
            } else {
                logger.warn("Usuario " + cedula + " no encontrado para restablecer contraseña");
            }
        } catch (Exception e) {
            logger.error("Error al restablecer contraseña: " + e.getMessage());
        }

        logger.info("Módulos disponibles: Autenticación, Usuarios, Citas");
        logger.info("==============================================");
    }
}
