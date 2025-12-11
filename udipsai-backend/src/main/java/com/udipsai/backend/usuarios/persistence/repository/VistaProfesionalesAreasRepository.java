package com.udipsai.backend.usuarios.persistence.repository;


import com.udipsai.backend.usuarios.persistence.view.VistaProfesionalesAreas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VistaProfesionalesAreasRepository extends JpaRepository<VistaProfesionalesAreas, Long> {

    // Método para encontrar profesionales por ID de área
    Page<VistaProfesionalesAreas> findByIdArea(Long idArea, Pageable pageable);
}