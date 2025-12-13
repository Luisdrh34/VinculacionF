package com.udipsai.backend.usuarios.service;

import com.udipsai.backend.common.service.UsuarioService;
import com.udipsai.backend.usuarios.persistence.repository.*;
import com.udipsai.backend.usuarios.persistence.view.VistaProfesionalesAreas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.udipsai.backend.usuarios.persistence.entity.ProfesionalEntity;
import com.udipsai.backend.usuarios.persistence.entity.RolEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioAreaEntity;
import com.udipsai.backend.common.exception.DataConflictException;
import com.udipsai.backend.common.exception.InvalidRequestBodyException;
import com.udipsai.backend.common.exception.ResourceNotFoundException;
import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolEntity;
import com.udipsai.backend.usuarios.service.dto.ProfesionalDTO;
import com.udipsai.backend.usuarios.service.dto.RegistrarProfesionalDTO;
import com.udipsai.backend.usuarios.service.dto.RolDTO;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;
import org.springframework.transaction.annotation.Transactional;

/*
 * Servicio para los Profesionales.
*/
@Service
public class ProfesionalService {
    @Autowired
    private ProfesionalRepository profesionalRepo;

    @Autowired
    private UsuarioService usuarioServ;

    @Autowired
    private VistaProfesionalesAreasRepository vistaProfesionalesAreasRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private AreaRepository areaRepo;

    private static final Logger logger = LoggerFactory.getLogger(ProfesionalService.class);

    // Mapear de un Profesional a DTO.
    public ProfesionalDTO mapearDTO(ProfesionalEntity prof) {
        ProfesionalDTO dto = new ProfesionalDTO();
        UsuarioEntity usuario = prof.getUsuario();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setIdProfesional(prof.getIdProfesional());
        dto.setCedula(usuario.getCedula());
        dto.setEstado(usuario.getEstado());
        dto.setProfEstado(prof.getEstado());
        dto.setNombres(usuario.getNombres());
        dto.setApellidos(usuario.getApellidos());
        dto.setEmail(usuario.getEmail());
        dto.setCelular(usuario.getCelular());
        dto.setEspecialidad(prof.getEspecialidad());
        if (usuario.getUsuarioRoles() != null) {
            dto.setRoles(usuario.getUsuarioRoles().stream()
                    .filter(usuarioRol -> usuarioRol.getEstado().equals("A"))
                    .map(usuarioRol -> {
                        RolEntity rol = usuarioRol.getRol();
                        RolDTO rolDTO = new RolDTO(
                                rol.getIdRol(),
                                rol.getNombre(),
                                rol.getEstado());
                        return rolDTO;
                    }).collect(Collectors.toSet()));
        }
        if (usuario.getUsuarioAreas() != null) {
            dto.setAreas(usuario.getUsuarioAreas().stream()
                    .filter(usuarioArea -> usuarioArea.getEstado().equals("A"))
                    .map(usuarioArea -> {
                        AreaEntity area = usuarioArea.getArea();
                        AreaDTO areaDTO = new AreaDTO(
                                area.getIdArea(),
                                area.getNombre(),
                                area.getEstado());
                        return areaDTO;
                    }).collect(Collectors.toSet()));
        }
        return dto;
    }

    // Obtener los Profesionales activos.
    public ResponseEntity<Page<ProfesionalDTO>> obtenerProfesionales(Pageable pageable) {
        logger.info("obtenerProfesionales()");
        logger.info("Obteniendo profesionales activos");
        Page<ProfesionalEntity> profesionalesA = profesionalRepo.findAllProfesionales(pageable);
        Page<ProfesionalDTO> profesionalesDTOs = profesionalesA.map(profesional -> mapearDTO(profesional));
        logger.info("200 OK: Profesionales activos obtenidos correctamente");
        return new ResponseEntity<>(profesionalesDTOs, HttpStatus.OK);
    }

    // Obtener un Profesional especifico.
    public ResponseEntity<ProfesionalDTO> obtenerProfesional(String cedula) {
        logger.info("obtenerProfesional()");
        logger.info("Obteniendo profesional con cedula: " + cedula);
        ProfesionalEntity profesionalEncontrado = profesionalRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Profesional con cedula " + cedula + " no encontrado"));

        ProfesionalDTO profesionalDTO = mapearDTO(profesionalEncontrado);
        logger.info("200 OK: Profesional obtenido correctamente");
        return new ResponseEntity<>(profesionalDTO, HttpStatus.OK);
    }

    // Obtener profesional por Id.
    public ResponseEntity<ProfesionalDTO> obtenerProfesionalPorId(Long id) {
        logger.info("obtenerProfesionalPorId()");
        logger.info("Obteniendo profesional con id: " + id);
        ProfesionalEntity profesionalEncontrado = profesionalRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Profesional con id " + id + " no encontrado"));

        ProfesionalDTO profesionalDTO = mapearDTO(profesionalEncontrado);
        logger.info("200 OK: Profesional obtenido correctamente");
        return new ResponseEntity<>(profesionalDTO, HttpStatus.OK);
    }

    // Obtener todos los Profesionales (A, I, B, N).
    public ResponseEntity<Page<ProfesionalDTO>> obtenerProfesionalesTodos(Pageable pageable) {
        logger.info("obtenerProfesionalesTodos()");
        logger.info("Obteniendo todos los profesionales");
        Page<ProfesionalEntity> profesionales = profesionalRepo.findAll(pageable);
        Page<ProfesionalDTO> profesionalesDTOs = profesionales.map(profesional -> mapearDTO(profesional));
        logger.info("200 OK: Todos los profesionales obtenidos correctamente");
        return new ResponseEntity<>(profesionalesDTOs, HttpStatus.OK);
    }

    // Obtener los Profesionales con estado especifico.
    public ResponseEntity<Page<ProfesionalDTO>> obtenerProfesionalesEstado(String estado, Pageable pageable) {
        logger.info("obtenerProfesionalesEstado()");
        logger.info("Obteniendo profesionales con estado {}", estado);
        Page<ProfesionalEntity> profesionalesI = profesionalRepo.findAllByEstado(estado, pageable);
        Page<ProfesionalDTO> profesionalesDTOs = profesionalesI.map(profesional -> mapearDTO(profesional));
        logger.info("200 OK: Profesionales con estado {} obtenidos correctamente", estado);
        return new ResponseEntity<>(profesionalesDTOs, HttpStatus.OK);
    }

    // Registrar un Profesional.
    @Transactional
    public ResponseEntity<ProfesionalDTO> registrarProfesional(RegistrarProfesionalDTO usuario) {
        logger.info("registrarProfesional()");
        logger.info("Registrando profesional con cedula: " + usuario.getCedula());

        ProfesionalEntity profesionalNuevo = new ProfesionalEntity();

        if (usuario.getCedula() == null || usuario.getContrasenia() == null || usuario.getNombres() == null
                || usuario.getApellidos() == null
                || usuario.getEmail() == null || usuario.getCelular() == null || usuario.getRoles() == null
                || usuario.getAreas() == null || usuario.getEspecialidad() == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para el registro");
        }

        profesionalNuevo.setEspecialidad(usuario.getEspecialidad());
        profesionalNuevo.setEstado("A");

        if (usuarioRepo.existsByCedula(usuario.getCedula())) {
            UsuarioEntity usuarioExistente = usuarioRepo.findByCedula(usuario.getCedula()).get();

            if (usuarioExistente.getEstado().equals("A")) {
                if (profesionalRepo.existsByUsuario_Cedula(usuario.getCedula())) {
                    throw new DataConflictException("Profesional ya existe con cedula " + usuario.getCedula());
                } else {
                    List<RolEntity> rolesEncontrados = rolRepo.findAllByNombre("DOCTOR");
                    if (rolesEncontrados.isEmpty()) {
                        throw new ResourceNotFoundException("Rol DOCTOR no encontrado en la base de datos");
                    }
                    RolEntity rolProfesional = rolesEncontrados.get(0);

                    if (rolProfesional.getEstado().equals("A")) {
                        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();

                        // Verificar si ya tiene el rol asignado (aunque sea inactivo)
                        java.util.Optional<UsuarioRolEntity> existingRol = usuarioExistente.getUsuarioRoles().stream()
                                .filter(ur -> ur.getRol().getIdRol().equals(rolProfesional.getIdRol()))
                                .findFirst();

                        if (existingRol.isPresent()) {
                            UsuarioRolEntity ur = existingRol.get();
                            ur.setEstado("A");
                            ur.setFechaAsignacion(localDateTimeUTC5);
                        } else {
                            UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
                            usuarioRol.setUsuario(usuarioExistente);
                            usuarioRol.setRol(rolProfesional);
                            usuarioRol.setFechaAsignacion(localDateTimeUTC5);
                            usuarioRol.setEstado("A");
                            usuarioExistente.getUsuarioRoles().add(usuarioRol);
                        }

                        usuarioRepo.save(usuarioExistente);

                        profesionalNuevo.setUsuario(usuarioExistente);
                    } else {
                        throw new DataConflictException("Rol DOCTOR no esta activo");
                    }
                }
            } else {
                throw new DataConflictException(
                        "Usuario ya existe con cedula " + usuario.getCedula() + " y estÃ¡ inactivo");
            }
        } else {
            UsuarioEntity usuarioNuevo = usuarioServ.registrarUsuario(usuario);
            List<RolEntity> rolesEncontrados = rolRepo.findAllByNombre("DOCTOR");
            if (rolesEncontrados.isEmpty()) {
                throw new ResourceNotFoundException("Rol DOCTOR no encontrado en la base de datos");
            }
            RolEntity rolProfesional = rolesEncontrados.get(0);

            if (rolProfesional != null && rolProfesional.getEstado().equals("A")) {
                UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
                usuarioRol.setUsuario(usuarioNuevo);
                usuarioRol.setRol(rolProfesional);

                ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
                usuarioRol.setFechaAsignacion(localDateTimeUTC5);
                usuarioRol.setEstado("A");

                Set<UsuarioRolEntity> roles = new HashSet<>();
                roles.add(usuarioRol);
                usuarioNuevo.setUsuarioRoles(roles);
                usuarioRepo.save(usuarioNuevo);

            } else {
                throw new DataConflictException("Rol PROFESIONAL no esta activo");
            }
            profesionalNuevo.setUsuario(usuarioNuevo);
        }

        ProfesionalEntity profesionalGuardado = profesionalRepo.save(profesionalNuevo);
        ProfesionalDTO profesionalDTO = mapearDTO(profesionalGuardado);
        logger.info("201 CREATED: Profesional registrado con datos: ");
        logger.info("Cedula: " + profesionalGuardado.getUsuario().getCedula());
        logger.info("Nombres: " + profesionalGuardado.getUsuario().getNombres());
        logger.info("Apellidos: " + profesionalGuardado.getUsuario().getApellidos());
        logger.info("Especialidad: " + profesionalGuardado.getEspecialidad());
        logger.info("Email: " + profesionalGuardado.getUsuario().getEmail());
        logger.info("Celular: " + profesionalGuardado.getUsuario().getCelular());
        logger.info("Fecha de creacion: " + profesionalGuardado.getUsuario().getFechaCreacion().toString());

        return new ResponseEntity<>(profesionalDTO, HttpStatus.CREATED);
    }

    // Actualizar un Profesional.
    public ResponseEntity<ProfesionalDTO> actualizarProfesional(Long id, ProfesionalDTO profesional) {
        logger.info("actualizarProfesional()");
        logger.info("Actualizando profesional con id: " + id);

        ProfesionalEntity profesionalEncontrado = profesionalRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Profesional con id " + id + " no encontrado"));

        if (profesional.getNombres() == null || profesional.getApellidos() == null || profesional.getEmail() == null
                || profesional.getCelular() == null || profesional.getAreas() == null
                || profesional.getEspecialidad() == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para la actualizacion de datos");
        }

        UsuarioEntity usuarioEncontrado = profesionalEncontrado.getUsuario();
        usuarioEncontrado.setNombres(profesional.getNombres());
        usuarioEncontrado.setApellidos(profesional.getApellidos());
        usuarioEncontrado.setEmail(profesional.getEmail());
        usuarioEncontrado.setCelular(profesional.getCelular());

        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
        usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);

        usuarioEncontrado.getUsuarioAreas().forEach(usuarioArea -> {
            usuarioArea.setEstado("N");
        });

        Set<UsuarioAreaEntity> usuarioAreas = profesional.getAreas().stream()
                .map(areaDTO -> {
                    AreaEntity area = areaRepo.findByNombre(areaDTO.getNombre()).get();

                    if (area != null && area.getEstado().equals("A")) {
                        UsuarioAreaEntity usuarioArea = new UsuarioAreaEntity();
                        usuarioArea.setUsuario(usuarioEncontrado);
                        usuarioArea.setArea(area);
                        usuarioArea.setFechaAsignacion(localDateTimeUTC5);
                        usuarioArea.setEstado("A");
                        return usuarioArea;
                    } else {
                        return null;
                    }
                }).collect(Collectors.toSet());

        usuarioEncontrado.setUsuarioAreas(usuarioAreas);

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);
        profesionalEncontrado.setUsuario(usuarioActualizado);
        profesionalEncontrado.setEspecialidad(profesional.getEspecialidad());
        ProfesionalEntity profesionalActualizado = profesionalRepo.save(profesionalEncontrado);
        ProfesionalDTO profesionalDTO = mapearDTO(profesionalActualizado);

        logger.info("200 OK: Profesional actualizado con datos:");
        logger.info("Cedula: " + profesionalActualizado.getUsuario().getCedula());
        logger.info("Nombres: " + profesionalActualizado.getUsuario().getNombres());
        logger.info("Apellidos: " + profesionalActualizado.getUsuario().getApellidos());
        logger.info("Especialidad: " + profesionalActualizado.getEspecialidad());
        logger.info("Email: " + profesionalActualizado.getUsuario().getEmail());
        logger.info("Celular: " + profesionalActualizado.getUsuario().getCelular());
        logger.info(
                "Fecha de modificacion: "
                        + profesionalActualizado.getUsuario().getFechaModificacion().toString());

        return new ResponseEntity<>(profesionalDTO, HttpStatus.OK);
    }

    // Eliminar un Profesional.
    public ResponseEntity<String> eliminarProfesional(Long id) {
        logger.info("eliminarProfesional()");
        logger.info("Eliminando profesional con id: " + id);
        ProfesionalEntity profesionalEncontrado = profesionalRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Profesional con id " + id + " no encontrado"));

        UsuarioEntity usuarioEncontrado = profesionalEncontrado.getUsuario();

        if (usuarioEncontrado.getUsuarioRoles().stream()
                .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("DOCTOR"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("N");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getRol().getNombre().equals("DOCTOR")) {
                usuarioRol.setEstado("N");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);
        profesionalEncontrado.setUsuario(usuarioActualizado);
        profesionalEncontrado.setEstado("N");
        profesionalRepo.save(profesionalEncontrado);
        logger.info("204 NO CONTENT: Profesional eliminado correctamente");
        return new ResponseEntity<>("Profesional eliminado correctamente", HttpStatus.NO_CONTENT);
    }

    // Activar un Profesional.
    public ResponseEntity<String> activarProfesional(String cedula) {
        logger.info("activarProfesional()");
        logger.info("Activando profesional con cedula: " + cedula);
        ProfesionalEntity profesionalEncontrado = profesionalRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Profesional con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = profesionalEncontrado.getUsuario();

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (!usuarioRol.getEstado().equals("N") && usuarioRol.getRol().getNombre().equals("DOCTOR")) {
                usuarioRol.setEstado("A");
            }
        });

        if (!usuarioEncontrado.getEstado().equals("N")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("DOCTOR"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("A");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (!profesionalEncontrado.getEstado().equals("N")) {
            profesionalEncontrado.setUsuario(usuarioActualizado);
            profesionalEncontrado.setEstado("A");
            profesionalRepo.save(profesionalEncontrado);
            logger.info("204 NO CONTENT: Profesional activado correctamente");
            return new ResponseEntity<>("Profesional activado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Profesional se encuentra eliminado");
        }
    }

    // Desactivar un Profesional.
    public ResponseEntity<String> desactivarProfesional(String cedula) {
        logger.info("desactivarProfesional()");
        logger.info("Desactivando profesional con cedula: " + cedula);
        ProfesionalEntity profesionalEncontrado = profesionalRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Profesional con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = profesionalEncontrado.getUsuario();

        if (usuarioEncontrado.getEstado().equals("A")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("DOCTOR"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("I");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("DOCTOR")) {
                usuarioRol.setEstado("I");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (profesionalEncontrado.getEstado().equals("A")) {
            profesionalEncontrado.setUsuario(usuarioActualizado);
            profesionalEncontrado.setEstado("I");
            profesionalRepo.save(profesionalEncontrado);
            logger.info("204 NO CONTENT: Profesional desactivado correctamente");
            return new ResponseEntity<>("Profesional desactivado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Profesional se encuentra desactivado, bloqueado o eliminado");
        }
    }

    // Bloquear un Profesional.
    public ResponseEntity<String> bloquearProfesional(String cedula) {
        logger.info("bloquearProfesional()");
        logger.info("Bloqueando profesional con cedula: " + cedula);
        ProfesionalEntity profesionalEncontrado = profesionalRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Profesional con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = profesionalEncontrado.getUsuario();

        if (usuarioEncontrado.getEstado().equals("A")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("DOCTOR"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("B");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("DOCTOR")) {
                usuarioRol.setEstado("B");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (profesionalEncontrado.getEstado().equals("A")) {
            profesionalEncontrado.setUsuario(usuarioActualizado);
            profesionalEncontrado.setEstado("B");
            profesionalRepo.save(profesionalEncontrado);
            logger.info("204 NO CONTENT: Profesional bloqueado correctamente");
            return new ResponseEntity<>("Profesional bloqueado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Profesional se encuentra desactivado, bloqueado o eliminado");
        }
    }

    // Obtener profesionales por area.
    public ResponseEntity<?> obtenerProfesionalesArea(Long area, Pageable pageable) {
        try {
            logger.info("obtenerProfesionalesArea()");
            logger.info("Obteniendo profesionales por area");
            Page<VistaProfesionalesAreas> profesionalesA = vistaProfesionalesAreasRepo.findByIdArea(area, pageable);
            logger.info("200 OK: Profesionales por area obtenidos correctamente");
            return new ResponseEntity<>(profesionalesA, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("400 BAD REQUEST: Error al obtener Profesionales por area");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Obtener profesional por usuario.
    public ResponseEntity<?> obtenerProfesionalPorUsuario(Long usuario) {
        logger.info("obtenerProfesionalPorUsuario()");
        logger.info("Obteniendo profesional por usuario");
        ProfesionalEntity profesionalEncontrado = profesionalRepo.findByUsuario_IdUsuario(usuario).orElseThrow(
                () -> new ResourceNotFoundException("Profesional con usuario " + usuario + " no encontrado"));
        ProfesionalDTO profesionalDTO = mapearDTO(profesionalEncontrado);
        logger.info("200 OK: Profesional obtenido correctamente");
        return new ResponseEntity<>(profesionalDTO, HttpStatus.OK);
    }

    // Obtener profesional por filtro
    public ResponseEntity<?> obtenerProfesionalesFiltro(String filtro, Pageable pageable) {
        logger.info("obtenerProfesionalesFiltro()");
        logger.info("Obteniendo profesionales por filtro");
        Page<ProfesionalEntity> profesionalesF = profesionalRepo.findProfesionalesFiltro(filtro, pageable);
        Page<ProfesionalDTO> profesionalesDTOs = profesionalesF.map(profesional -> mapearDTO(profesional));
        logger.info("200 OK: Profesionales por filtro obtenidos correctamente");
        return new ResponseEntity<>(profesionalesDTOs, HttpStatus.OK);
    }
}
