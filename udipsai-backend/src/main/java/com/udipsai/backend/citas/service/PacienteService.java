package com.udipsai.backend.citas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import com.udipsai.backend.common.exception.InvalidRequestBodyException;
import com.udipsai.backend.common.exception.ResourceNotFoundException;
import com.udipsai.backend.citas.persistence.entity.PacienteEntity;
import com.udipsai.backend.citas.persistence.repository.PacienteRepository;
import com.udipsai.backend.citas.service.dto.PacienteDTO;

/*
 * Servicio para los Pacientes.
*/
@Service
public class PacienteService {
    @Autowired
    private PacienteRepository pacienteRepo;

    private static final Logger logger = LoggerFactory.getLogger(PacienteService.class);

    // Mapear de un Paciente a DTO.
    public PacienteDTO mapearDTO(PacienteEntity paciente) {
        PacienteDTO dto = new PacienteDTO(
                paciente.getId(),
                paciente.getCedula() != null ? paciente.getCedula().trim() : null,
                paciente.getFechaApertura(),
                paciente.getFechaNacimiento(),
                paciente.getEdad() != null ? paciente.getEdad().trim() : null,
                paciente.getNombresApellidos() != null ? paciente.getNombresApellidos().trim() : null,
                paciente.getTelefono() != null ? paciente.getTelefono().trim() : null,
                paciente.getCelular() != null ? paciente.getCelular().trim() : null,
                paciente.getCiudad() != null ? paciente.getCiudad().trim() : null,
                paciente.getBarrio() != null ? paciente.getBarrio().trim() : null,
                paciente.getDomicilio() != null ? paciente.getDomicilio().trim() : null,
                paciente.getInstitucionEducativa() != null ? paciente.getInstitucionEducativa().trim() : null,
                paciente.getTipoInstitucion() != null ? paciente.getTipoInstitucion().trim() : null,
                paciente.getSector() != null ? paciente.getSector().trim() : null,
                paciente.getJornada() != null ? paciente.getJornada().trim() : null,
                paciente.getTelefonoInstitucion() != null ? paciente.getTelefonoInstitucion().trim() : null,
                paciente.getAnioEducacion() != null ? paciente.getAnioEducacion().trim() : null,
                paciente.getParalelo() != null ? paciente.getParalelo().trim() : null,
                paciente.getPerteneceInclusion() != null ? paciente.getPerteneceInclusion().trim() : null,
                paciente.getTieneDiscapacidad() != null ? paciente.getTieneDiscapacidad().trim() : null,
                paciente.getPortadorCarnet() != null ? paciente.getPortadorCarnet().trim() : null,
                paciente.getDiagnostico() != null ? paciente.getDiagnostico().trim() : null,
                paciente.getMotivoConsulta() != null ? paciente.getMotivoConsulta().trim() : null,
                paciente.getObservaciones() != null ? paciente.getObservaciones().trim() : null,
                paciente.getNombreExaminador() != null ? paciente.getNombreExaminador().trim() : null,
                paciente.getAnotaciones() != null ? paciente.getAnotaciones().trim() : null);
        return dto;
    }

    // Mapear de una lista de Pacientes a una lista de DTOs.
    public List<PacienteDTO> mapearDTOs(List<PacienteEntity> pacientes) {
        List<PacienteDTO> dtos = pacientes
                .stream()
                .map(usr -> mapearDTO(usr))
                .toList();
        return dtos;
    }

    // Obtener todos los Pacientes.
    public ResponseEntity<Page<PacienteEntity>> obtenerPacientes(Pageable pageable) {
        logger.info("obtenerPacientes()");
        logger.info("Obteniendo todos los pacientes");
        Page<PacienteEntity> pacientesPage = pacienteRepo.findAll(pageable);
        logger.info("200 OK: Pacientes obtenidos correctamente");

        return new ResponseEntity<>(pacientesPage, HttpStatus.OK);
    }

    // Obtener un Paciente especifico por Id.
    public ResponseEntity<?> obtenerPacientePorId(Long id) {
        logger.info("obtenerPacientePorId()");
        logger.info("Obteniendo paciente con nro de ficha {}", id);

        if (id == null) {
            throw new InvalidRequestBodyException("El nro de ficha del paciente no puede ser nulo");
        }

        PacienteEntity paciente = pacienteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente con nro de ficha " + id + " no encontrado"));

        PacienteDTO pacienteDTO = mapearDTO(paciente);
        logger.info("200 OK: Paciente con nro de ficha {} obtenido correctamente", id);

        return new ResponseEntity<>(pacienteDTO, HttpStatus.OK);
    }

    // Obtener un Paciente o lista de Pacientes por filtro de búsqueda.
    public ResponseEntity<Page<PacienteEntity>> obtenerPacientesPorFiltro(String filtro,
            Pageable pageable) {
        try {
            logger.info("obtenerPacientesPorFiltro()");
            logger.info("Obteniendo pacientes por filtro");

            Page<PacienteEntity> pacientes = pacienteRepo.findByFilters(filtro, pageable);

            if (pacientes.isEmpty()) {
                logger.info("404 NOT FOUND: No se encontraron pacientes por filtro");
                throw new ResourceNotFoundException("No se encontraron pacientes por filtro");
            }


            logger.info("200 OK: Pacientes obtenidos por filtro correctamente");

            return new ResponseEntity<>(pacientes, HttpStatus.OK);
        }catch (Exception e) {
            logger.error("500 INTERNAL SERVER ERROR: Error al obtener pacientes por filtro");
            throw e;
        }


    }
}

