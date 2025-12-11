package com.udipsai.backend.citas.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.udipsai.backend.citas.persistence.entity.PacienteEntity;

import java.util.Optional;

/*
 * Repositorio de la entidad Paciente.
*/
@Repository
public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {

    Optional<PacienteEntity> findById(Long id);

    boolean existsById(Long id);

    Page<PacienteEntity> findAll(Pageable pageable);

    Page<PacienteEntity> findAllByCedula(String cedula, Pageable pageable);

    Page<PacienteEntity> findAllByNombresApellidos(String nombresApellidos, Pageable pageable);

    @Query("SELECT p FROM PacienteEntity p WHERE " +
            "(:filtro IS NULL OR " +
            "CAST(p.id AS text) LIKE CONCAT('%', :filtro, '%') OR " +
            "UPPER(p.nombresApellidos) LIKE UPPER(CONCAT('%', :filtro, '%')))")
    Page<PacienteEntity> findByFilters(@Param("filtro") String filtro, Pageable pageable);

}
