package com.udipsai.backend.auth.service;

import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;
import com.udipsai.backend.usuarios.persistence.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioSecurityService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioSecurityService.class);

    @Autowired
    public UsuarioSecurityService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String cedula) throws UsernameNotFoundException {
        logger.info("loadUserByUsername()");
        logger.info("Cargando usuario con cÃ©dula " + cedula);
        Optional<UsuarioEntity> usuarioOpt = this.usuarioRepository.findByCedula(cedula);

        if (usuarioOpt.isEmpty()) {
            logger.error("Usuario con cÃ©dula " + cedula + " no encontrado");
            throw new UsernameNotFoundException("Usuario con cÃ©dula " + cedula + " no encontrado");
        }

        UsuarioEntity usuarioEntity = usuarioOpt.get();

        String[] roles = usuarioEntity.getUsuarioRoles().stream()
                .filter(usuarioRol -> usuarioRol.getRol().getEstado().equals("A"))
                .map(usuarioRol -> usuarioRol.getRol().getNombre())
                .toArray(String[]::new);

        logger.info("Usuario encontrado");

        return User.builder()
                .username(usuarioEntity.getCedula())
                .password(usuarioEntity.getContrasenia())
                .authorities(this.grantedAuthorities(roles))
                .disabled("I".equals(usuarioEntity.getEstado()))
                .accountLocked("B".equals(usuarioEntity.getEstado()))
                .build();
    }

    private List<GrantedAuthority> grantedAuthorities(String[] roles) {
        List<GrantedAuthority> authorities = new ArrayList<>(roles.length);

        for (String rol : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));
        }

        return authorities;
    }
}
