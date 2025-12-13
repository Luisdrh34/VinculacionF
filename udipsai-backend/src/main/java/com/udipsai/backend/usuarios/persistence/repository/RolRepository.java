package com.udipsai.backend.usuarios.persistence.repository;

import com.udipsai.backend.usuarios.persistence.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Repositorio de la entidad Rol.
*/
@Repository
public interface RolRepository extends JpaRepository<RolEntity, Long> {
    Optional<RolEntity> findByNombre(String nombre);

    List<RolEntity> findAllByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<RolEntity> findAllByEstado(String estado);
}
