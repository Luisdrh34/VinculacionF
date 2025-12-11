package com.udipsai.backend.usuarios.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import com.udipsai.backend.common.exception.DataConflictException;
import com.udipsai.backend.common.exception.ResourceNotFoundException;
import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;
import com.udipsai.backend.usuarios.persistence.repository.AreaRepository;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;

/*
 * Servicio para las Areas.
*/
@Service

public class AreaService {
    @Autowired
    private AreaRepository areaRepository;

    private static final Logger logger = LoggerFactory.getLogger(AreaService.class);

    // Mapear de una Area a DTO.
    public AreaDTO mapearDTO(AreaEntity area) {
        AreaDTO dto = new AreaDTO();
        dto.setIdArea(area.getIdArea());
        dto.setNombre(area.getNombre());
        dto.setEstado(area.getEstado());
        return dto;
    }

    // Obtener las Areas activas.
    public ResponseEntity<List<AreaDTO>> obtenerAreas() {
        logger.info("obtenerAreas()");
        logger.info("Obteniendo todas las areas activas");
        List<AreaDTO> areas = areaRepository.findAllByEstado("A").stream().map(area -> mapearDTO(area)).toList();
        logger.info("200 OK: Areas activas obtenidas correctamente");

        return new ResponseEntity<>(areas, HttpStatus.OK);
    }

    // Obtener todas las Areas.
    public ResponseEntity<List<AreaDTO>> obtenerAreasTodas() {
        logger.info("obtenerAreasTodas()");
        logger.info("Obteniendo todas las areas");
        List<AreaDTO> areas = areaRepository.findAll().stream().map(area -> mapearDTO(area)).toList();
        logger.info("200 OK: Todas las areas obtenidas correctamente");

        return new ResponseEntity<>(areas, HttpStatus.OK);
    }

    // Obtener Area por su id.
    public ResponseEntity<AreaDTO> obtenerAreaPorId(Long id) {
        logger.info("obtenerAreaPorId()");
        logger.info("Obteniendo el area con id {}", id);
        AreaEntity area = areaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Area con id " + id + " no encontrada"));

        logger.info("200 OK: Area con id {} obtenida correctamente", id);
        AreaDTO dto = mapearDTO(area);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Obtener Area por su nombre.
    public ResponseEntity<AreaDTO> obtenerAreaPorNombre(String nombre) {
        logger.info("obtenerAreaPorNombre()");
        logger.info("Obteniendo el area con nombre {}", nombre);
        AreaEntity area = areaRepository.findByNombre(nombre).orElseThrow(
                () -> new ResourceNotFoundException("Area con nombre " + nombre + " no encontrada"));

        logger.info("200 OK: Area con nombre {} obtenida correctamente", nombre);
        AreaDTO dto = mapearDTO(area);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Obtener Areas con estado especifico.
    public ResponseEntity<List<AreaDTO>> obtenerAreasEstado(String estado) {
        logger.info("obtenerAreasEstado()");
        logger.info("Obteniendo todas las areas con estado {}", estado);
        List<AreaDTO> areas = areaRepository.findAllByEstado(estado).stream().map(area -> mapearDTO(area)).toList();
        logger.info("200 OK: Areas con estado {} obtenidas correctamente", estado);
        return new ResponseEntity<>(areas, HttpStatus.OK);
    }

    // Registrar Area.
    public ResponseEntity<AreaDTO> registrarArea(String nombre) {
        logger.info("registrarArea()");
        logger.info("Registrando el area con nombre {}", nombre);

        if (areaRepository.existsByNombre(nombre)) {
            throw new DataConflictException("Ya existe un area con nombre " + nombre);
        }

        AreaEntity area = new AreaEntity();
        area.setNombre(nombre);
        area.setEstado("A");
        area = areaRepository.save(area);
        AreaDTO areaDTO = mapearDTO(area);
        logger.info("201 CREATED: Area {} registrada correctamente", nombre);
        return new ResponseEntity<>(areaDTO, HttpStatus.CREATED);
    }

    // Actualizar Area.
    public ResponseEntity<AreaDTO> actualizarArea(Long id, String nombre) {
        logger.info("actualizarArea()");
        logger.info("Actualizando el area con id {}", id);

        AreaEntity area = areaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Area con id " + id + " no encontrada"));

        if (areaRepository.existsByNombre(nombre)) {
            throw new DataConflictException("Ya existe un area con nombre " + nombre);
        }

        area.setNombre(nombre);
        area = areaRepository.save(area);
        AreaDTO areaDTO = mapearDTO(area);
        logger.info("200 OK: Area {} actualizada correctamente", area.getNombre());
        return new ResponseEntity<>(areaDTO, HttpStatus.OK);
    }

    // Eliminar Area.
    public ResponseEntity<String> eliminarArea(Long id) {
        logger.info("eliminarArea()");
        logger.info("Eliminando el area con id {}", id);

        AreaEntity area = areaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Area con id " + id + " no encontrada"));

        area.setEstado("N");
        areaRepository.save(area);
        logger.info("204 NO_CONTENT: Area {} eliminada correctamente", area.getNombre());
        return new ResponseEntity<>("Area eliminada correctamente", HttpStatus.NO_CONTENT);
    }

    // Activar Area.
    public ResponseEntity<String> activarArea(Long id) {
        logger.info("activarArea()");
        logger.info("Activando el area con id {}", id);

        AreaEntity area = areaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Area con id " + id + " no encontrada"));

        if (!area.getEstado().equals("N")) {
            area.setEstado("A");
            areaRepository.save(area);
            logger.info("200 OK: Area {} activada correctamente", area.getNombre());
            return new ResponseEntity<>("Area activada correctamente", HttpStatus.OK);
        } else {
            throw new DataConflictException("Area " + area.getNombre() + " no puede ser activada porque fue eliminada");
        }
    }

    // Desactivar Area.
    public ResponseEntity<String> desactivarArea(Long id) {
        logger.info("desactivarArea()");
        logger.info("Desactivando el area con id {}", id);

        AreaEntity area = areaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Area con id " + id + " no encontrada"));

        if (!area.getEstado().equals("N")) {
            area.setEstado("I");
            areaRepository.save(area);
            logger.info("200 OK: Area {} desactivada correctamente", area.getNombre());
            return new ResponseEntity<>("Area desactivada correctamente", HttpStatus.OK);
        } else {
            throw new DataConflictException(
                    "Area " + area.getNombre() + " no puede ser desactivada porque fue eliminada");
        }
    }

    // Bloquear Area.
    public ResponseEntity<String> bloquearArea(Long id) {
        logger.info("bloquearArea()");
        logger.info("Bloqueando el area con id {}", id);

        AreaEntity area = areaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Area con id " + id + " no encontrada"));

        if (!area.getEstado().equals("N")) {
            area.setEstado("B");
            areaRepository.save(area);
            logger.info("200 OK: Area {} bloqueada correctamente", area.getNombre());
            return new ResponseEntity<>("Area bloqueada correctamente", HttpStatus.OK);
        } else {
            throw new DataConflictException(
                    "Area " + area.getNombre() + " no puede ser bloqueada porque fue eliminada");
        }
    }

}
