package com.udipsai.backend.citas.web.controller;

import com.udipsai.backend.citas.persistence.entity.PacienteEntity;
import com.udipsai.backend.citas.persistence.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.udipsai.backend.citas.service.PacienteService;
import com.udipsai.backend.citas.service.dto.PacienteDTO;

/*
 * Controlador para los Pacientes.
*/
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {
    @Autowired
    private PacienteService pacienteServ;

    @Autowired
    private PacienteRepository pacienteRepo;

    // Obtener los Pacientes.
    @GetMapping
    public ResponseEntity<Page<PacienteEntity>> obtenerPacientes(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return pacienteServ.obtenerPacientes(pageable);
    }

    // Obtener un Paciente o lista de Pacientes por filtro.
    @GetMapping("/filtro")
    public ResponseEntity<?> obtenerPacientesPorFiltro(@RequestParam(required = false) Long id,
                                                       @RequestParam(name = "filtro", required = true) String filtro,
                                                       @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return pacienteServ.obtenerPacientesPorFiltro(filtro, pageable);
    }


}

