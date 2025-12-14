package com.udipsai.backend.citas.web.controller;

import com.udipsai.backend.citas.service.ReporteCitaService;
import com.udipsai.backend.citas.service.dto.ReporteCitaRespuestaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/citas/reporte")
public class ReporteCitaController {

    @Autowired
    private ReporteCitaService reporteCitaService;

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<ReporteCitaRespuestaDTO> obtenerReportePorPaciente(@PathVariable Integer idPaciente) {
        ReporteCitaRespuestaDTO reporte = reporteCitaService.generarReportePorPaciente(idPaciente);
        return ResponseEntity.ok(reporte);
    }
}
