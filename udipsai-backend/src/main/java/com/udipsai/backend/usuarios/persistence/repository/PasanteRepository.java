package com.udipsai.backend.usuarios.persistence.repository;

import com.udipsai.backend.usuarios.persistence.entity.PasanteEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * Repositorio de la entidad Pasante.
*/
@Repository
public interface PasanteRepository extends JpaRepository<PasanteEntity, Long> {
    Optional<PasanteEntity> findByUsuario_Cedula(String cedula);

    boolean existsByUsuario_Cedula(String cedula);

    Page<PasanteEntity> findAll(Pageable pageable);

    Page<PasanteEntity> findAllByEstado(String estado, Pageable pageable);

    Page<PasanteEntity> findAllByUsuario_Nombres(String nombres, Pageable pageable);

    Page<PasanteEntity> findAllByUsuario_Apellidos(String apellidos, Pageable pageable);

    @Query("SELECT p FROM PasanteEntity p WHERE p.estado IN ('A', 'B')")
    Page<PasanteEntity> findAllPasantes(Pageable pageable);

    // obtener pasantes filtro
    @Query("SELECT p FROM PasanteEntity p WHERE " +
            "(:filtro IS NULL OR " +
            "UPPER(p.usuario.nombres) LIKE UPPER(CONCAT('%', :filtro, '%')) OR " +
            "UPPER(p.usuario.apellidos) LIKE UPPER(CONCAT('%', :filtro, '%')) OR " +
            "UPPER(p.usuario.cedula) LIKE UPPER(CONCAT('%', :filtro, '%'))) " +
            "AND p.estado != 'N'")
    Page<PasanteEntity> findPasantesFiltro(@Param("filtro") String filtro, Pageable pageable);

}
