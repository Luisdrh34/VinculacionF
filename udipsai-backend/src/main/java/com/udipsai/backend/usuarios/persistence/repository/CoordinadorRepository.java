package com.udipsai.backend.usuarios.persistence.repository;

import com.udipsai.backend.usuarios.persistence.entity.CoordinadorEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * Repositorio de la entidad Coordinador.
*/
@Repository
public interface CoordinadorRepository extends JpaRepository<CoordinadorEntity, Long> {
    Optional<CoordinadorEntity> findByUsuario_Cedula(String cedula);

    boolean existsByUsuario_Cedula(String cedula);

    Page<CoordinadorEntity> findAll(Pageable pageable);

    Page<CoordinadorEntity> findAllByEstado(String estado, Pageable pageable);

    Page<CoordinadorEntity> findAllByUsuario_Nombres(String nombres, Pageable pageable);

    Page<CoordinadorEntity> findAllByUsuario_Apellidos(String apellidos, Pageable pageable);

    @Query("SELECT c FROM CoordinadorEntity c WHERE c.estado IN ('A', 'B')")
    Page<CoordinadorEntity> findAllCoordinadores(Pageable pageable);

    // Obtener coordinadores que por filtro LIKE (nombre, apellido, cedula)
    @Query("SELECT c FROM CoordinadorEntity c WHERE " +
            "(:filtro IS NULL OR " +
            "UPPER(c.usuario.nombres) LIKE UPPER(CONCAT('%', :filtro, '%')) OR " +
            "UPPER(c.usuario.apellidos) LIKE UPPER(CONCAT('%', :filtro, '%')) OR " +
            "UPPER(c.usuario.cedula) LIKE UPPER(CONCAT('%', :filtro, '%'))) " +
            "AND c.estado != 'N'")
    Page<CoordinadorEntity> findCoordinadoresFiltro(@Param("filtro") String filtro, Pageable pageable);

}
