package com.udipsai.backend.usuarios.persistence.repository;

import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/*
 * Repositorio de la entidad Area.
*/
@Repository
public interface AreaRepository extends JpaRepository<AreaEntity, Long> {
    Optional<AreaEntity> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<AreaEntity> findAllByEstado(String estado);
}
