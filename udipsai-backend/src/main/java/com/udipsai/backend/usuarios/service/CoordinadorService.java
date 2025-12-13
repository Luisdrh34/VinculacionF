package com.udipsai.backend.usuarios.service;

import com.udipsai.backend.common.service.UsuarioService;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.udipsai.backend.usuarios.persistence.entity.CoordinadorEntity;
import com.udipsai.backend.usuarios.persistence.entity.RolEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioAreaEntity;
import com.udipsai.backend.common.exception.DataConflictException;
import com.udipsai.backend.common.exception.InvalidRequestBodyException;
import com.udipsai.backend.common.exception.ResourceNotFoundException;
import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolEntity;
import com.udipsai.backend.usuarios.persistence.repository.AreaRepository;
import com.udipsai.backend.usuarios.persistence.repository.CoordinadorRepository;
import com.udipsai.backend.usuarios.persistence.repository.RolRepository;
import com.udipsai.backend.usuarios.persistence.repository.UsuarioRepository;
import com.udipsai.backend.usuarios.service.dto.CoordinadorDTO;
import com.udipsai.backend.usuarios.service.dto.RegistrarCoordinadorDTO;
import com.udipsai.backend.usuarios.service.dto.RolDTO;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;
import org.springframework.transaction.annotation.Transactional;

/*
 * Servicio para los Coordinadores.
*/
@Service
public class CoordinadorService {
    @Autowired
    private CoordinadorRepository coordinadorRepo;

    @Autowired
    private UsuarioService usuarioServ;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private AreaRepository areaRepo;

    private static final Logger logger = LoggerFactory.getLogger(CoordinadorService.class);

    // Mapear de un Coordinador a DTO.
    public CoordinadorDTO mapearDTO(CoordinadorEntity coor) {
        CoordinadorDTO dto = new CoordinadorDTO();
        UsuarioEntity usuario = coor.getUsuario();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setIdCoordinador(coor.getIdCoordinador());
        dto.setCedula(usuario.getCedula());
        dto.setEstado(usuario.getEstado());
        dto.setCoorEstado(coor.getEstado());
        dto.setNombres(usuario.getNombres());
        dto.setApellidos(usuario.getApellidos());
        dto.setEmail(usuario.getEmail());
        dto.setCelular(usuario.getCelular());
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

    // Obtener los Coordinadores activos.
    public ResponseEntity<Page<CoordinadorDTO>> obtenerCoordinadores(Pageable pageable) {
        logger.info("obtenerCoordinadores()");
        logger.info("Obteniendo coordinadores activos");
        Page<CoordinadorEntity> coordinadoresA = coordinadorRepo.findAllCoordinadores(pageable);
        Page<CoordinadorDTO> coordinadoresDTOs = coordinadoresA.map(coor -> mapearDTO(coor));
        logger.info("200 OK: Coordinadores activos obtenidos correctamente");

        return new ResponseEntity<>(coordinadoresDTOs, HttpStatus.OK);
    }

    // Obtener un Coordinador especifico.
    public ResponseEntity<CoordinadorDTO> obtenerCoordinador(String cedula) {
        logger.info("obtenerCoordinador()");
        logger.info("Obteniendo coordinador con cedula: " + cedula);
        CoordinadorEntity coordinadorEncontrado = coordinadorRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Coordinador con cedula " + cedula + " no encontrado"));

        CoordinadorDTO coordinadorDTO = mapearDTO(coordinadorEncontrado);
        logger.info("200 OK: Coordinador obtenido correctamente");
        return new ResponseEntity<>(coordinadorDTO, HttpStatus.OK);
    }

    // Obtener todos los Coordinadores (A, I, B, N).
    public ResponseEntity<Page<CoordinadorDTO>> obtenerCoordinadoresTodos(Pageable pageable) {
        logger.info("obtenerCoordinadoresTodos()");
        logger.info("Obteniendo todos los coordinadores");
        Page<CoordinadorEntity> coordinadores = coordinadorRepo.findAll(pageable);
        Page<CoordinadorDTO> coordinadoresDTOs = coordinadores.map(coordinador -> mapearDTO(coordinador));
        logger.info("200 OK: Todos los coordinadores obtenidos correctamente");
        return new ResponseEntity<>(coordinadoresDTOs, HttpStatus.OK);
    }

    // Obtener los Coordinadores con estado especifico.
    public ResponseEntity<Page<CoordinadorDTO>> obtenerCoordinadoresEstado(String estado, Pageable pageable) {
        logger.info("obtenerCoordinadoresEstado()");
        logger.info("Obteniendo coordinadores con estado {}", estado);
        Page<CoordinadorEntity> coordinadoresE = coordinadorRepo.findAllByEstado(estado, pageable);
        Page<CoordinadorDTO> coordinadoresDTOs = coordinadoresE.map(coordinador -> mapearDTO(coordinador));
        logger.info("200 OK: Coordinadores con estado {} obtenidos correctamente", estado);
        return new ResponseEntity<>(coordinadoresDTOs, HttpStatus.OK);
    }

    // Registrar un Coordinador.
    @Transactional
    public ResponseEntity<CoordinadorDTO> registrarCoordinador(RegistrarCoordinadorDTO usuario) {
        logger.info("registrarCoordinador()");
        logger.info("Registrando coordinador con cedula: " + usuario.getCedula());

        if (usuario.getCedula() == null || usuario.getContrasenia() == null || usuario.getNombres() == null
                || usuario.getApellidos() == null
                || usuario.getEmail() == null || usuario.getCelular() == null || usuario.getRoles() == null
                || usuario.getAreas() == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para el registro");
        }

        CoordinadorEntity coordinadorNuevo = new CoordinadorEntity();

        coordinadorNuevo.setEstado("A");

        if (usuarioRepo.existsByCedula(usuario.getCedula())) {
            UsuarioEntity usuarioExistente = usuarioRepo.findByCedula(usuario.getCedula()).get();

            if (usuarioExistente.getEstado().equals("A")) {
                if (coordinadorRepo.existsByUsuario_Cedula(usuario.getCedula())) {
                    throw new DataConflictException(
                            "Coordinador ya existe con cedula " + usuario.getCedula() + " o email "
                                    + usuarioExistente.getEmail());
                } else {
                    List<RolEntity> rolesEncontrados = rolRepo.findAllByNombre("COORDINADORA");
                    if (rolesEncontrados.isEmpty()) {
                        throw new ResourceNotFoundException("Rol COORDINADORA no encontrado en la base de datos");
                    }
                    RolEntity rolProfesional = rolesEncontrados.get(0);

                    if (rolProfesional.getEstado().equals("A")) {
                        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();

                        // Verificar si ya tiene el rol
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

                        coordinadorNuevo.setUsuario(usuarioExistente);
                    } else {
                        throw new DataConflictException("Rol COORDINADORA no esta activo");
                    }
                }
            } else {
                throw new DataConflictException(
                        "Usuario ya existe con cedula " + usuario.getCedula() + " y está inactivo");
            }
        } else {
            UsuarioEntity usuarioNuevo = usuarioServ.registrarUsuario(usuario);
            coordinadorNuevo.setUsuario(usuarioNuevo);

            List<RolEntity> rolesEncontrados = rolRepo.findAllByNombre("COORDINADORA");
            if (rolesEncontrados.isEmpty()) {
                throw new ResourceNotFoundException("Rol COORDINADORA no encontrado en la base de datos");
            }
            RolEntity rolProfesional = rolesEncontrados.get(0);

            if (rolProfesional.getEstado().equals("A")) {
                UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
                usuarioRol.setUsuario(usuarioNuevo);
                usuarioRol.setRol(rolProfesional);

                ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
                usuarioRol.setFechaAsignacion(localDateTimeUTC5);
                usuarioRol.setEstado("A");

                usuarioNuevo.getUsuarioRoles().add(usuarioRol);
                usuarioRepo.save(usuarioNuevo);

                coordinadorNuevo.setUsuario(usuarioNuevo);
            } else {
                throw new DataConflictException("Rol COORDINADORA no esta activo");
            }

        }

        CoordinadorEntity coordinadorGuardado = coordinadorRepo.save(coordinadorNuevo);
        CoordinadorDTO coordinadorDTO = mapearDTO(coordinadorGuardado);

        logger.info("201 CREATED: Coordinador registrado con datos: ");
        logger.info("Cedula: " + coordinadorGuardado.getUsuario().getCedula());
        logger.info("Nombres: " + coordinadorGuardado.getUsuario().getNombres());
        logger.info("Apellidos: " + coordinadorGuardado.getUsuario().getApellidos());
        logger.info("Email: " + coordinadorGuardado.getUsuario().getEmail());
        logger.info("Celular: " + coordinadorGuardado.getUsuario().getCelular());
        logger.info("Fecha de creacion: " + coordinadorGuardado.getUsuario().getFechaCreacion().toString());

        return new ResponseEntity<>(coordinadorDTO, HttpStatus.CREATED);
    }

    // Actualizar un Coordinador.
    public ResponseEntity<CoordinadorDTO> actualizarCoordinador(String cedula, CoordinadorDTO coordinador) {
        logger.info("actualizarCoordinador()");
        logger.info("Actualizando coordinador con cedula: " + cedula);

        CoordinadorEntity coordinadorEncontrado = coordinadorRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Coordinador con cedula " + cedula + " no encontrado"));

        if (coordinador.getNombres() == null || coordinador.getApellidos() == null || coordinador.getEmail() == null
                || coordinador.getCelular() == null || coordinador.getAreas() == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para la actualizacion de datos");
        }

        UsuarioEntity usuarioEncontrado = coordinadorEncontrado.getUsuario();
        usuarioEncontrado.setNombres(coordinador.getNombres());
        usuarioEncontrado.setApellidos(coordinador.getApellidos());
        usuarioEncontrado.setEmail(coordinador.getEmail());
        usuarioEncontrado.setCelular(coordinador.getCelular());

        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
        usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);

        usuarioEncontrado.getUsuarioAreas().forEach(usuarioArea -> {
            usuarioArea.setEstado("N");
        });

        Set<UsuarioAreaEntity> usuarioAreas = coordinador.getAreas().stream()
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
        coordinadorEncontrado.setUsuario(usuarioActualizado);
        CoordinadorEntity coordinadorActualizado = coordinadorRepo.save(coordinadorEncontrado);
        CoordinadorDTO coordinadorDTO = mapearDTO(coordinadorActualizado);

        logger.info("200 OK: Coordinador actualizado con datos:");
        logger.info("Cedula: " + coordinadorActualizado.getUsuario().getCedula());
        logger.info("Nombres: " + coordinadorActualizado.getUsuario().getNombres());
        logger.info("Apellidos: " + coordinadorActualizado.getUsuario().getApellidos());
        logger.info("Email: " + coordinadorActualizado.getUsuario().getEmail());
        logger.info("Celular: " + coordinadorActualizado.getUsuario().getCelular());
        logger.info(
                "Fecha de modificacion: "
                        + coordinadorActualizado.getUsuario().getFechaModificacion().toString());

        return new ResponseEntity<>(coordinadorDTO, HttpStatus.OK);
    }

    // Eliminar un Coordinador.
    public ResponseEntity<String> eliminarCoordinador(String cedula) {
        logger.info("eliminarCoordinador()");
        logger.info("Eliminando coordinador con cedula: " + cedula);

        CoordinadorEntity coordinadorEncontrado = coordinadorRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Coordinador con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = coordinadorEncontrado.getUsuario();

        if (usuarioEncontrado.getUsuarioRoles().stream()
                .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("COORDINADOR"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("N");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getRol().getNombre().equals("COORDINADOR")) {
                usuarioRol.setEstado("N");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);
        coordinadorEncontrado.setUsuario(usuarioActualizado);
        coordinadorEncontrado.setEstado("N");
        coordinadorRepo.save(coordinadorEncontrado);
        logger.info("204 NO CONTENT: Coordinador eliminado correctamente");

        return new ResponseEntity<>("Coordinador eliminado correctamente", HttpStatus.NO_CONTENT);
    }

    // Activar un Coordinador.
    public ResponseEntity<String> activarCoordinador(String cedula) {
        logger.info("activarCoordinador()");
        logger.info("Activando coordinador con cedula: " + cedula);

        CoordinadorEntity coordinadorEncontrado = coordinadorRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Coordinador con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = coordinadorEncontrado.getUsuario();

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (!usuarioRol.getEstado().equals("N") && usuarioRol.getRol().getNombre().equals("COORDINADOR")) {
                usuarioRol.setEstado("A");
            }
        });

        if (!usuarioEncontrado.getEstado().equals("N")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("COORDINADOR"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("A");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (!coordinadorEncontrado.getEstado().equals("N")) {
            coordinadorEncontrado.setUsuario(usuarioActualizado);
            coordinadorEncontrado.setEstado("A");
            coordinadorRepo.save(coordinadorEncontrado);
            logger.info("204 NO CONTENT: Coordinador activado correctamente");

            return new ResponseEntity<>("Coordinador activado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Coordinador se encuentra eliminado");
        }
    }

    // Desactivar un Coordinador.
    public ResponseEntity<String> desactivarCoordinador(String cedula) {
        logger.info("desactivarCoordinador()");
        logger.info("Desactivando coordinador con cedula: " + cedula);
        CoordinadorEntity coordinadorEncontrado = coordinadorRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Coordinador con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = coordinadorEncontrado.getUsuario();

        if (usuarioEncontrado.getEstado().equals("A")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("COORDINADOR"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("I");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("COORDINADOR")) {
                usuarioRol.setEstado("I");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (coordinadorEncontrado.getEstado().equals("A")) {
            coordinadorEncontrado.setUsuario(usuarioActualizado);
            coordinadorEncontrado.setEstado("I");
            coordinadorRepo.save(coordinadorEncontrado);
            logger.info("204 NO CONTENT: Coordinador desactivado correctamente");

            return new ResponseEntity<>("Coordinador desactivado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Coordinador fue desactivado, bloqueado o eliminado");
        }
    }

    // Bloquear un Coordinador.
    public ResponseEntity<String> bloquearCoordinador(String cedula) {
        logger.info("bloquearCoordinador()");
        logger.info("Bloqueando coordinador con cedula: " + cedula);

        CoordinadorEntity coordinadorEncontrado = coordinadorRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Coordinador con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = coordinadorEncontrado.getUsuario();

        if (usuarioEncontrado.getEstado().equals("A")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("COORDINADOR"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("B");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("COORDINADOR")) {
                usuarioRol.setEstado("B");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (coordinadorEncontrado.getEstado().equals("A")) {
            coordinadorEncontrado.setUsuario(usuarioActualizado);
            coordinadorEncontrado.setEstado("B");
            coordinadorRepo.save(coordinadorEncontrado);
            logger.info("204 NO CONTENT: Coordinador bloqueado correctamente");

            return new ResponseEntity<>("Coordinador bloqueado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Coordinador fue desactivado, bloqueado o eliminado");
        }
    }

    // Busqueda por filtro.
    public ResponseEntity<Page<CoordinadorDTO>> findCoordinadoresFiltro(String filtro, Pageable pageable) {
        logger.info("findCoordinadoresFiltro()");
        logger.info("Buscando coordinadores con filtro: " + filtro);
        Page<CoordinadorEntity> coordinadores = coordinadorRepo.findCoordinadoresFiltro(filtro, pageable);
        Page<CoordinadorDTO> coordinadoresDTOs = coordinadores.map(coordinador -> mapearDTO(coordinador));
        logger.info("200 OK: Coordinadores con filtro " + filtro + " encontrados correctamente");
        return new ResponseEntity<>(coordinadoresDTOs, HttpStatus.OK);
    }

}
