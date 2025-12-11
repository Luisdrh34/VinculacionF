package com.udipsai.backend.usuarios.persistence.repository;

import com.udipsai.backend.usuarios.persistence.entity.ProfesionalEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * Repositorio de la entidad Profesional.
*/
@Repository
public interface ProfesionalRepository extends JpaRepository<ProfesionalEntity, Long> {
    Optional<ProfesionalEntity> findByUsuario_Cedula(String cedula);

    boolean existsByUsuario_Cedula(String cedula);

    Page<ProfesionalEntity> findAll(Pageable pageable);

    Page<ProfesionalEntity> findAllByEstado(String estado, Pageable pageable);

    Page<ProfesionalEntity> findAllByUsuario_Nombres(String nombres, Pageable pageable);

    Page<ProfesionalEntity> findAllByUsuario_Apellidos(String apellidos, Pageable pageable);

    // Obtener profesional por usuario
    Optional<ProfesionalEntity> findByUsuario_IdUsuario(Long id);

    @Query("SELECT p FROM ProfesionalEntity p WHERE p.estado IN ('A', 'B')")
    Page<ProfesionalEntity> findAllProfesionales(Pageable pageable);

    // Obtener profesional por filtro
    @Query("SELECT p FROM ProfesionalEntity p WHERE " +
            "(:filtro IS NULL OR " +
            "UPPER(p.usuario.nombres) LIKE UPPER(CONCAT('%', :filtro, '%')) OR " +
            "UPPER(p.usuario.apellidos) LIKE UPPER(CONCAT('%', :filtro, '%')) OR " +
            "UPPER(p.usuario.cedula) LIKE UPPER(CONCAT('%', :filtro, '%'))) " +
            "AND p.estado != 'N'")
    Page<ProfesionalEntity> findProfesionalesFiltro(String filtro, Pageable pageable);

}
