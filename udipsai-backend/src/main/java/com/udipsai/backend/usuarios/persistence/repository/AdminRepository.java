package com.udipsai.backend.usuarios.persistence.repository;

import com.udipsai.backend.usuarios.persistence.entity.AdminEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * Repositorio de la entidad Admin.
*/
@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    Optional<AdminEntity> findByUsuario_Cedula(String cedula);

    boolean existsByUsuario_Cedula(String cedula);

    Page<AdminEntity> findAll(Pageable pageable);

    Page<AdminEntity> findAllByEstado(String estado, Pageable pageable);

    Page<AdminEntity> findAllByUsuario_Nombres(String nombres, Pageable pageable);

    Page<AdminEntity> findAllByUsuario_Apellidos(String apellidos, Pageable pageable);
}
