package com.udipsai.backend.citas.service;

import com.udipsai.backend.citas.persistence.entity.VistaCitasCompleta;
import com.udipsai.backend.citas.persistence.repository.VistaCitasCompletaRepository;
import com.udipsai.backend.citas.service.dto.ReporteCitaDTO;
import com.udipsai.backend.citas.service.dto.ReporteCitaRespuestaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.udipsai.backend.usuarios.persistence.repository.ProfesionalRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteCitaService {

    @Autowired
    private VistaCitasCompletaRepository vistaCitasCompletaRepository;

    @Autowired
    private com.udipsai.backend.citas.persistence.repository.PacienteRepository pacienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    public ReporteCitaRespuestaDTO generarReportePorPaciente(Integer fichaPaciente) {
        // Obtener las ultimas 20 citas
        // Ordenamos por fecha de creacion de la cita de forma descendente para obtener
        // las mas recientes
        Pageable pageable = PageRequest.of(0, 20, Sort.by("fecha").descending());
        Page<VistaCitasCompleta> paginaCitas = vistaCitasCompletaRepository.findByFichaPaciente(fichaPaciente,
                pageable);

        List<VistaCitasCompleta> listaCitas = paginaCitas.getContent();

        ReporteCitaRespuestaDTO respuesta = new ReporteCitaRespuestaDTO();

        // Fetch Patient Name Correctly
        String nombrePaciente = "Desconocido";
        if (fichaPaciente != null) {
            nombrePaciente = pacienteRepository.findById(Long.valueOf(fichaPaciente))
                    .map(p -> p.getNombresApellidos())
                    .orElse("Desconocido");
        }
        respuesta.setPacienteNombreCompleto(nombrePaciente);

        if (listaCitas.isEmpty()) {
            respuesta.setCitas(List.of());
            return respuesta;
        }

        // Removed incorrect logic relying on view's ambiguous columns
        // VistaCitasCompleta primeraCita = listaCitas.get(0);
        // String nombreCompleto = ...

        // Mapear a DTOs de reporte
        List<ReporteCitaDTO> citasDTO = listaCitas.stream().map(cita -> {
            // Conversión segura de Time a LocalTime si es necesario, o uso directo si ya es
            // compatible
            LocalTime horaInicio = null;
            if (cita.getHorainicio() != null) {
                horaInicio = cita.getHorainicio().toLocalTime();
            }

            String nombreProfesional = "Desconocido";
            if (cita.getIdProfesional() != null) {
                // Obtener el profesional y su usuario asociado para sacar el nombre
                nombreProfesional = profesionalRepository.findById(Long.valueOf(cita.getIdProfesional()))
                        .map(p -> p.getUsuario().getNombres() + " " + p.getUsuario().getApellidos())
                        .orElse("Desconocido");
            }

            return new ReporteCitaDTO(
                    cita.getFecha(),
                    horaInicio,
                    nombreProfesional,
                    cita.getNombreArea());
        }).collect(Collectors.toList());

        respuesta.setCitas(citasDTO);

        return respuesta;
    }
}
