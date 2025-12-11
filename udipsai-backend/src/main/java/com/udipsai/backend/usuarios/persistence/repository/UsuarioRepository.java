package com.udipsai.backend.usuarios.persistence.repository;

import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/*
 * Repositorio de la entidad Usuario.
*/
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByCedula(String cedula);

    Page<UsuarioEntity> findAll(Pageable pageable);

    Page<UsuarioEntity> findAllByNombres(String nombres, Pageable pageable);

    Page<UsuarioEntity> findAllByApellidos(String apellidos, Pageable pageable);

    Page<UsuarioEntity> findAllByEstado(String estado, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByCedula(String cedula);
    
    @Modifying
    @Transactional
    void deleteByCedula(String cedula);
}
