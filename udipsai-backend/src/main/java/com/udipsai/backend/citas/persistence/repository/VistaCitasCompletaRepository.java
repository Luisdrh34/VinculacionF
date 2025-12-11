package com.udipsai.backend.citas.persistence.repository;


import com.udipsai.backend.citas.persistence.entity.VistaCitasCompleta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VistaCitasCompletaRepository extends JpaRepository<VistaCitasCompleta, Integer> {
    Page<VistaCitasCompleta> findByFichaPaciente(Integer fichaPaciente, Pageable pageable);
}