package com.udipsai.backend.auth.service;

import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolId;
import com.udipsai.backend.auth.persistence.repository.UsuarioRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioRolService {

    @Autowired
    private UsuarioRolRepository usuarioRolRepository;

    public List<UsuarioRolEntity> getAllUsuarioRoles() {
        return usuarioRolRepository.findAll();
    }

    public Optional<UsuarioRolEntity> getUsuarioRolById(Long idUsuario, Long rol) {
        return usuarioRolRepository.findById(new UsuarioRolId(idUsuario, rol));
    }

    public UsuarioRolEntity createUsuarioRol(UsuarioRolEntity usuarioRol) {
        return usuarioRolRepository.save(usuarioRol);
    }

    public UsuarioRolEntity updateUsuarioRol(Long idUsuario, Long rol, UsuarioRolEntity updatedUsuarioRol) {
        Optional<UsuarioRolEntity> existingUsuarioRol = usuarioRolRepository.findById(new UsuarioRolId(idUsuario, rol));
        if (existingUsuarioRol.isPresent()) {
            UsuarioRolEntity usuarioRolEntity = existingUsuarioRol.get();
            usuarioRolEntity.setFechaAsignacion(updatedUsuarioRol.getFechaAsignacion());
            return usuarioRolRepository.save(usuarioRolEntity);
        } else {
            throw new RuntimeException("UsuarioRol not found");
        }
    }

    public void deleteUsuarioRol(Long idUsuario, Long rol) {
        usuarioRolRepository.deleteById(new UsuarioRolId(idUsuario, rol));
    }
}

