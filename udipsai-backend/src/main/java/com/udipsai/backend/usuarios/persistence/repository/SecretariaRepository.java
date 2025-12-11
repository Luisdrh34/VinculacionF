package com.udipsai.backend.usuarios.persistence.repository;

import com.udipsai.backend.usuarios.persistence.entity.SecretariaEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * Repositorio de la entidad Secretaria.
*/
@Repository
public interface SecretariaRepository extends JpaRepository<SecretariaEntity, Long> {
    Optional<SecretariaEntity> findByUsuario_Cedula(String cedula);

    boolean existsByUsuario_Cedula(String cedula);

    Page<SecretariaEntity> findAll(Pageable pageable);

    Page<SecretariaEntity> findAllByEstado(String estado, Pageable pageable);

    Page<SecretariaEntity> findAllByUsuario_Nombres(String nombres, Pageable pageable);

    Page<SecretariaEntity> findAllByUsuario_Apellidos(String apellidos, Pageable pageable);

    @Query("SELECT s FROM SecretariaEntity s WHERE s.estado IN ('A', 'B')")
    Page<SecretariaEntity> findAllSecretarias(Pageable pageable);

    // obtener secretarias filtro
    @Query("SELECT s FROM SecretariaEntity s WHERE " +
            "(:filtro IS NULL OR " +
            "UPPER(s.usuario.nombres) LIKE UPPER(CONCAT('%', :filtro, '%')) OR " +
            "UPPER(s.usuario.apellidos) LIKE UPPER(CONCAT('%', :filtro, '%')) OR " +
            "UPPER(s.usuario.cedula) LIKE UPPER(CONCAT('%', :filtro, '%'))) " +
            "AND s.estado != 'N'")
    Page<SecretariaEntity> findSecretariasFiltro(String filtro, Pageable pageable);
}
