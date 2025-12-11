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
import com.udipsai.backend.usuarios.persistence.entity.RolEntity;
import com.udipsai.backend.usuarios.persistence.repository.RolRepository;
import com.udipsai.backend.usuarios.service.dto.RolDTO;

/*
 * Servicio para los Roles.
*/
@Service
public class RolService {
    @Autowired
    private RolRepository rolRepo;

    private static final Logger logger = LoggerFactory.getLogger(RolService.class);

    // Mapear de un Rol a DTO.
    public RolDTO mapearDTO(RolEntity rol) {
        RolDTO dto = new RolDTO();
        dto.setIdRol(rol.getIdRol());
        dto.setNombre(rol.getNombre());
        dto.setEstado(rol.getEstado());
        return dto;
    }

    // Obtener los Roles activos.
    public ResponseEntity<List<RolDTO>> obtenerRoles() {
        logger.info("obtenerRoles()");
        logger.info("Obteniendo todos los roles activos");
        List<RolDTO> roles = rolRepo.findAllByEstado("A").stream().map(rol -> mapearDTO(rol)).toList();
        logger.info("200 OK: Roles activos obtenidos correctamente");
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // Obtener todos los Roles.
    public ResponseEntity<List<RolDTO>> obtenerRolesTodos() {
        logger.info("obtenerRolesTodos()");
        logger.info("Obteniendo todos los roles");
        List<RolDTO> roles = rolRepo.findAll().stream().map(rol -> mapearDTO(rol)).toList();
        logger.info("200 OK: Todos los roles obtenidos correctamente");
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // Obtener Rol por su id.
    public ResponseEntity<RolDTO> obtenerRolPorId(Long id) {
        logger.info("obtenerRolPorId()");
        logger.info("Obteniendo el rol con id {}", id);
        RolEntity rolEncontrado = rolRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Rol con id " + id + " no encontrado"));
        logger.info("200 OK: Rol con id {} obtenido correctamente", id);
        RolDTO dto = mapearDTO(rolEncontrado);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Obtener Rol por su nombre.
    public ResponseEntity<RolDTO> obtenerRolPorNombre(String nombre) {
        logger.info("obtenerRolPorNombre()");
        logger.info("Obteniendo el rol con nombre {}", nombre);
        RolEntity rol = rolRepo.findByNombre(nombre).orElseThrow(
                () -> new ResourceNotFoundException("Rol con nombre " + nombre + " no encontrado"));
        logger.info("200 OK: Rol con nombre {} obtenido correctamente", nombre);
        RolDTO dto = mapearDTO(rol);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Obtener Roles con estado especifico.
    public ResponseEntity<List<RolDTO>> obtenerRolesEstado(String estado) {
        logger.info("obtenerRolesEstado()");
        logger.info("Obteniendo todos los roles con estado {}", estado);
        List<RolDTO> roles = rolRepo.findAllByEstado(estado).stream().map(rol -> mapearDTO(rol)).toList();
        logger.info("200 OK: Roles con estado {} obtenidos correctamente", estado);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // Registrar Rol.
    public ResponseEntity<RolDTO> registrarRol(String nombre) {
        logger.info("registrarRol()");
        logger.info("Registrando el rol con nombre {}", nombre);

        if (rolRepo.existsByNombre(nombre)) {
            throw new DataConflictException("Ya existe un rol con nombre " + nombre);
        }

        RolEntity rol = new RolEntity();
        rol.setNombre(nombre);
        rol.setEstado("A");
        rol = rolRepo.save(rol);
        RolDTO rolDTO = mapearDTO(rol);
        logger.info("201 CREATED: Rol {} registrado correctamente", nombre);
        return new ResponseEntity<>(rolDTO, HttpStatus.CREATED);
    }

    // Actualizar Rol.
    public ResponseEntity<RolDTO> actualizarRol(Long id, String nombre) {
        logger.info("actualizarRol()");
        logger.info("Actualizando el rol con id {}", id);
        RolEntity rol = rolRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Rol con id " + id + " no encontrado"));

        if (rolRepo.existsByNombre(nombre)) {
            throw new DataConflictException("Ya existe un rol con nombre " + nombre);
        }

        rol.setNombre(nombre);
        rol = rolRepo.save(rol);
        RolDTO rolDTO = mapearDTO(rol);
        logger.info("200 OK: Rol con id {} y nombre {} actualizado correctamente", rol.getIdRol(),
                rol.getNombre());
        return new ResponseEntity<>(rolDTO, HttpStatus.OK);
    }

    // Eliminar Rol.
    public ResponseEntity<String> eliminarRol(Long id) {
        logger.info("eliminarRol()");
        logger.info("Eliminando el rol con id {}", id);
        RolEntity rol = rolRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Rol con id " + id + " no encontrado"));
        rol.setEstado("N");
        rolRepo.save(rol);
        logger.info("200 OK: Rol con id {} eliminado correctamente", id);
        return new ResponseEntity<>("Rol eliminado correctamente", HttpStatus.OK);
    }

    // Activar Rol.
    public ResponseEntity<String> activarRol(Long id) {
        logger.info("activarRol()");
        logger.info("Activando el rol con id {}", id);
        RolEntity rol = rolRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Rol con id " + id + " no encontrado"));

        if (!rol.getEstado().equals("N")) {
            rol.setEstado("A");
            rolRepo.save(rol);
            logger.info("200 OK: Rol con id {} activado correctamente", id);
            return new ResponseEntity<>("Rol activado correctamente", HttpStatus.OK);
        } else {
            throw new DataConflictException("Rol " + rol.getNombre() + " fue eliminado");
        }
    }

    // Desactivar Rol.
    public ResponseEntity<String> desactivarRol(Long id) {
        logger.info("desactivarRol()");
        logger.info("Desactivando el rol con id {}", id);
        RolEntity rol = rolRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Rol con id " + id + " no encontrado"));

        if (rol.getEstado().equals("A")) {
            rol.setEstado("I");
            rolRepo.save(rol);
            logger.info("200 OK: Rol con id {} desactivado correctamente", id);
            return new ResponseEntity<>("Rol desactivado correctamente", HttpStatus.OK);
        } else {
            throw new DataConflictException("Rol " + rol.getNombre() + " fue desactivado, bloqueado o eliminado");
        }
    }

    // Bloquear Rol.
    public ResponseEntity<String> bloquearRol(Long id) {
        logger.info("bloquearRol()");
        logger.info("Bloqueando el rol con id {}", id);
        RolEntity rol = rolRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Rol con id " + id + " no encontrado"));

        if (rol.getEstado().equals("A")) {
            rol.setEstado("B");
            rolRepo.save(rol);
            logger.info("200 OK: Rol con id {} bloqueado correctamente", id);
            return new ResponseEntity<>("Rol bloqueado correctamente", HttpStatus.OK);
        } else {
            throw new DataConflictException("Rol " + rol.getNombre() + " fue desactivado, bloqueado o eliminado");
        }
    }
}
