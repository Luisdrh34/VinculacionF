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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.udipsai.backend.usuarios.persistence.entity.PasanteEntity;
import com.udipsai.backend.usuarios.persistence.entity.RolEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioAreaEntity;
import com.udipsai.backend.common.exception.DataConflictException;
import com.udipsai.backend.common.exception.InvalidRequestBodyException;
import com.udipsai.backend.common.exception.ResourceNotFoundException;
import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolEntity;
import com.udipsai.backend.usuarios.persistence.repository.AreaRepository;
import com.udipsai.backend.usuarios.persistence.repository.PasanteRepository;
import com.udipsai.backend.usuarios.persistence.repository.RolRepository;
import com.udipsai.backend.usuarios.persistence.repository.UsuarioRepository;
import com.udipsai.backend.usuarios.service.dto.PasanteDTO;
import com.udipsai.backend.usuarios.service.dto.RegistrarPasanteDTO;
import com.udipsai.backend.usuarios.service.dto.RolDTO;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;
import org.springframework.transaction.annotation.Transactional;

/*
 * Servicio para los Pasantes.
*/
@Service
public class PasanteService {
    @Autowired
    private PasanteRepository pasanteRepo;

    @Autowired
    private UsuarioService usuarioServ;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private AreaRepository areaRepo;

    private static final Logger logger = LoggerFactory.getLogger(PasanteService.class);

    // Mapear de un Pasante a DTO.
    public PasanteDTO mapearDTO(PasanteEntity pas) {
        PasanteDTO dto = new PasanteDTO();
        UsuarioEntity usuario = pas.getUsuario();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setIdPasante(pas.getIdPasante());
        dto.setCedula(usuario.getCedula());
        dto.setEstado(usuario.getEstado());
        dto.setPasEstado(pas.getEstado());
        dto.setNombres(usuario.getNombres());
        dto.setApellidos(usuario.getApellidos());
        dto.setEmail(usuario.getEmail());
        dto.setCelular(usuario.getCelular());
        dto.setCarrera(pas.getCarrera());
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

    // Obtener los Pasantes activos.
    public ResponseEntity<Page<PasanteDTO>> obtenerPasantes(Pageable pageable) {
        logger.info("obtenerPasantes()");
        logger.info("Obteniendo pasantes activos");
        Page<PasanteEntity> pasantesA = pasanteRepo.findAllPasantes(pageable);
        Page<PasanteDTO> pasantesDTOs = pasantesA.map(pasante -> mapearDTO(pasante));
        logger.info("200 OK: Pasantes activos obtenidos correctamente");
        return new ResponseEntity<>(pasantesDTOs, HttpStatus.OK);
    }

    // Obtener un Pasante especifico.
    public ResponseEntity<PasanteDTO> obtenerPasante(String cedula) {
        logger.info("obtenerPasante()");
        logger.info("Obteniendo pasante con cedula: " + cedula);
        PasanteEntity pasanteEncontrado = pasanteRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Pasante con cedula " + cedula + " no encontrado"));

        PasanteDTO pasanteDTO = mapearDTO(pasanteEncontrado);
        logger.info("200 OK: Pasante obtenido correctamente");
        return new ResponseEntity<>(pasanteDTO, HttpStatus.OK);
    }

    // Obtener todos los Pasantes (A, I, B, N).
    public ResponseEntity<Page<PasanteDTO>> obtenerPasantesTodos(Pageable pageable) {
        logger.info("obtenerPasantesTodos()");
        logger.info("Obteniendo todos los pasantes");
        Page<PasanteEntity> pasantesPage = pasanteRepo.findAll(pageable);
        Page<PasanteDTO> pasantesDTOs = pasantesPage.map(pasante -> mapearDTO(pasante));
        logger.info("200 OK: Todos los pasantes obtenidos correctamente");
        return new ResponseEntity<>(pasantesDTOs, HttpStatus.OK);
    }

    // Obtener los Pasantes con estado especifico.
    public ResponseEntity<Page<PasanteDTO>> obtenerPasantesEstado(String estado, Pageable pageable) {
        logger.info("obtenerPasantesEstado()");
        logger.info("Obteniendo pasantes con estado {}", estado);
        Page<PasanteEntity> pasantesI = pasanteRepo.findAllByEstado(estado, pageable);
        Page<PasanteDTO> pasantesDTOs = pasantesI.map(pasante -> mapearDTO(pasante));
        logger.info("200 OK: Pasantes con estado {} obtenidos correctamente", estado);
        return new ResponseEntity<>(pasantesDTOs, HttpStatus.OK);
    }

    // Registrar un Pasante.
    @Transactional
    public ResponseEntity<PasanteDTO> registrarPasante(RegistrarPasanteDTO usuario) {
        logger.info("registrarPasante()");
        logger.info("Registrando pasante con cedula: " + usuario.getCedula());

        PasanteEntity pasanteNuevo = new PasanteEntity();

        if (usuario.getCedula() == null || usuario.getContrasenia() == null || usuario.getNombres() == null
                || usuario.getApellidos() == null
                || usuario.getEmail() == null || usuario.getCelular() == null || usuario.getRoles() == null
                || usuario.getAreas() == null || usuario.getCarrera() == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para el registro");
        }

        pasanteNuevo.setCarrera(usuario.getCarrera());
        pasanteNuevo.setEstado("A");

        if (usuarioRepo.existsByCedula(usuario.getCedula())) {
            UsuarioEntity usuarioExistente = usuarioRepo.findByCedula(usuario.getCedula()).get();

            if (usuarioExistente.getEstado().equals("A")) {
                if (pasanteRepo.existsByUsuario_Cedula(usuario.getCedula())) {
                    throw new DataConflictException(
                            "Pasante ya existe con cedula " + usuario.getCedula() + " o email "
                                    + usuarioExistente.getEmail());
                } else {
                    List<RolEntity> rolesEncontrados = rolRepo.findAllByNombre("PASANTES");
                    if (rolesEncontrados.isEmpty()) {
                        throw new ResourceNotFoundException("Rol PASANTES no encontrado en la base de datos");
                    }
                    RolEntity rolPasante = rolesEncontrados.get(0);

                    if (rolPasante.getEstado().equals("A")) {
                        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();

                        // Verificar si ya tiene el rol
                        java.util.Optional<UsuarioRolEntity> existingRol = usuarioExistente.getUsuarioRoles().stream()
                                .filter(ur -> ur.getRol().getIdRol().equals(rolPasante.getIdRol()))
                                .findFirst();

                        if (existingRol.isPresent()) {
                            UsuarioRolEntity ur = existingRol.get();
                            ur.setEstado("A");
                            ur.setFechaAsignacion(localDateTimeUTC5);
                        } else {
                            UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
                            usuarioRol.setUsuario(usuarioExistente);
                            usuarioRol.setRol(rolPasante);
                            usuarioRol.setFechaAsignacion(localDateTimeUTC5);
                            usuarioRol.setEstado("A");
                            usuarioExistente.getUsuarioRoles().add(usuarioRol);
                        }

                        usuarioRepo.save(usuarioExistente);

                        pasanteNuevo.setUsuario(usuarioExistente);
                    } else {
                        throw new DataConflictException("Rol PASANTES no esta activo");
                    }
                }
            } else {
                throw new DataConflictException(
                        "Usuario ya existe con cedula " + usuario.getCedula() + " y está inactivo");
            }
        } else {
            UsuarioEntity usuarioNuevo = usuarioServ.registrarUsuario(usuario);
            pasanteNuevo.setUsuario(usuarioNuevo);

            List<RolEntity> rolesEncontrados = rolRepo.findAllByNombre("PASANTES");
            if (rolesEncontrados.isEmpty()) {
                throw new ResourceNotFoundException("Rol PASANTES no encontrado en la base de datos");
            }
            RolEntity rolPasante = rolesEncontrados.get(0);

            if (rolPasante.getEstado().equals("A")) {
                UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
                usuarioRol.setUsuario(usuarioNuevo);
                usuarioRol.setRol(rolPasante);

                ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
                usuarioRol.setFechaAsignacion(localDateTimeUTC5);
                usuarioRol.setEstado("A");

                Set<UsuarioRolEntity> usuarioRoles = new HashSet<>();
                usuarioRoles.add(usuarioRol);
                usuarioNuevo.setUsuarioRoles(usuarioRoles);

                usuarioRepo.save(usuarioNuevo);

                pasanteNuevo.setUsuario(usuarioNuevo);
            } else {
                throw new DataConflictException("Rol PASANTE no esta activo");
            }
        }

        PasanteEntity pasanteGuardado = pasanteRepo.save(pasanteNuevo);
        PasanteDTO pasanteDTO = mapearDTO(pasanteGuardado);

        logger.info("201 CREATED: Pasante registrado con datos: ");
        logger.info("Cedula: " + pasanteGuardado.getUsuario().getCedula());
        logger.info("Nombres: " + pasanteGuardado.getUsuario().getNombres());
        logger.info("Apellidos: " + pasanteGuardado.getUsuario().getApellidos());
        logger.info("Carrera: " + pasanteGuardado.getCarrera());
        logger.info("Email: " + pasanteGuardado.getUsuario().getEmail());
        logger.info("Celular: " + pasanteGuardado.getUsuario().getCelular());
        logger.info("Fecha de creacion: " + pasanteGuardado.getUsuario().getFechaCreacion().toString());

        return new ResponseEntity<>(pasanteDTO, HttpStatus.CREATED);
    }

    // Actualizar un Pasante.
    public ResponseEntity<PasanteDTO> actualizarPasante(String cedula, PasanteDTO pasante) {
        logger.info("actualizarPasante()");
        logger.info("Actualizando pasante con cedula: " + cedula);

        PasanteEntity pasanteEncontrado = pasanteRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Pasante con cedula " + cedula + " no encontrado"));

        if (pasante.getNombres() == null || pasante.getApellidos() == null || pasante.getEmail() == null
                || pasante.getCelular() == null || pasante.getAreas() == null || pasante.getCarrera() == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para la actualizacion de datos");
        }

        UsuarioEntity usuarioEncontrado = pasanteEncontrado.getUsuario();

        usuarioEncontrado.setNombres(pasante.getNombres());
        usuarioEncontrado.setApellidos(pasante.getApellidos());
        usuarioEncontrado.setEmail(pasante.getEmail());
        usuarioEncontrado.setCelular(pasante.getCelular());

        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
        usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);

        usuarioEncontrado.getUsuarioAreas().forEach(usuarioArea -> {
            usuarioArea.setEstado("N");
        });

        Set<UsuarioAreaEntity> usuarioAreas = pasante.getAreas().stream()
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

        pasanteEncontrado.setUsuario(usuarioActualizado);
        pasanteEncontrado.setCarrera(pasante.getCarrera());

        PasanteEntity pasanteActualizado = pasanteRepo.save(pasanteEncontrado);
        PasanteDTO pasanteDTO = mapearDTO(pasanteActualizado);
        logger.info("200 OK: Pasante actualizado con datos:");
        logger.info("Cedula: " + pasanteActualizado.getUsuario().getCedula());
        logger.info("Nombres: " + pasanteActualizado.getUsuario().getNombres());
        logger.info("Apellidos: " + pasanteActualizado.getUsuario().getApellidos());
        logger.info("Carrera: " + pasanteActualizado.getCarrera());
        logger.info("Email: " + pasanteActualizado.getUsuario().getEmail());
        logger.info("Celular: " + pasanteActualizado.getUsuario().getCelular());
        logger.info(
                "Fecha de modificacion: " + pasanteActualizado.getUsuario().getFechaModificacion().toString());

        return new ResponseEntity<>(pasanteDTO, HttpStatus.OK);
    }

    // Eliminar un Pasante.
    public ResponseEntity<String> eliminarPasante(Long id) {
        logger.info("eliminarPasante()");
        logger.info("Eliminando pasante con id: " + id);
        Optional<PasanteEntity> pasanteOpt = Optional.of(pasanteRepo.getReferenceById(id));

        if (pasanteOpt.isPresent()) {
            PasanteEntity pasanteEncontrado = pasanteOpt.get();
            UsuarioEntity usuarioEncontrado = pasanteEncontrado.getUsuario();

            if (usuarioEncontrado.getUsuarioRoles().stream()
                    .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                    && usuarioEncontrado.getUsuarioRoles().stream()
                            .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                    && usuarioRol.getRol().getNombre().equals("PASANTE"))
                            .toList().size() == 1) {
                usuarioEncontrado.setEstado("N");
                ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
                usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
            }

            usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
                if (usuarioRol.getRol().getNombre().equals("PASANTE")) {
                    usuarioRol.setEstado("N");
                }
            });

            UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

            pasanteEncontrado.setUsuario(usuarioActualizado);
            pasanteEncontrado.setEstado("N");
            pasanteRepo.save(pasanteEncontrado);
            logger.info("204 NO CONTENT: Pasante eliminado correctamente");

            return new ResponseEntity<>("Pasante eliminado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new ResourceNotFoundException("Pasante con id " + id + " no encontrado");
        }
    }

    // Activar un Pasante.
    public ResponseEntity<String> activarPasante(String cedula) {
        logger.info("activarPasante()");
        logger.info("Activando pasante con cedula: " + cedula);
        PasanteEntity pasanteEncontrado = pasanteRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Pasante con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = pasanteEncontrado.getUsuario();
        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (!usuarioRol.getEstado().equals("N") && usuarioRol.getRol().getNombre().equals("PASANTE")) {
                usuarioRol.setEstado("A");
            }
        });

        if (!usuarioEncontrado.getEstado().equals("N")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("PASANTE"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("A");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (!pasanteEncontrado.getEstado().equals("N")) {
            pasanteEncontrado.setUsuario(usuarioActualizado);
            pasanteEncontrado.setEstado("A");
            pasanteRepo.save(pasanteEncontrado);
            logger.info("204 NO CONTENT: Pasante activado correctamente");
            return new ResponseEntity<>("Pasante activado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Pasante se encuentra eliminado");
        }
    }

    // Desactivar un Pasante.
    public ResponseEntity<String> desactivarPasante(String cedula) {
        logger.info("desactivarPasante()");
        logger.info("Desactivando pasante con cedula: " + cedula);
        PasanteEntity pasanteEncontrado = pasanteRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Pasante con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = pasanteEncontrado.getUsuario();
        if (usuarioEncontrado.getEstado().equals("A")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("PASANTE"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("I");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("PASANTE")) {
                usuarioRol.setEstado("I");
            }
        });
        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (pasanteEncontrado.getEstado().equals("A")) {
            pasanteEncontrado.setUsuario(usuarioActualizado);
            pasanteEncontrado.setEstado("I");
            pasanteRepo.save(pasanteEncontrado);
            logger.info("204 NO CONTENT: Pasante desactivado correctamente");
            return new ResponseEntity<>("Pasante desactivado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Pasante se encuentra desactivado, bloqueado o eliminado");
        }
    }

    // Bloquear un Pasante.
    public ResponseEntity<String> bloquearPasante(Long id) {
        logger.info("bloquearPasante()");
        logger.info("Bloqueando pasante con id: " + id);
        Optional<PasanteEntity> pasanteOpt = Optional.of(pasanteRepo.getReferenceById(id));

        if (pasanteOpt.isPresent()) {
            PasanteEntity pasanteEncontrado = pasanteOpt.get();
            UsuarioEntity usuarioEncontrado = pasanteEncontrado.getUsuario();

            if (usuarioEncontrado.getEstado().equals("A")
                    && usuarioEncontrado.getUsuarioRoles().stream()
                            .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                    && usuarioEncontrado.getUsuarioRoles().stream()
                            .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                    && usuarioRol.getRol().getNombre().equals("PASANTE"))
                            .toList().size() == 1) {
                usuarioEncontrado.setEstado("B");
                ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
                usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
            }

            usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
                if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("PASANTE")) {
                    usuarioRol.setEstado("B");
                }
            });
            UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

            if (pasanteEncontrado.getEstado().equals("A")) {
                pasanteEncontrado.setUsuario(usuarioActualizado);
                pasanteEncontrado.setEstado("B");
                pasanteRepo.save(pasanteEncontrado);
                logger.info("204 NO CONTENT: Pasante bloqueado correctamente");
                return new ResponseEntity<>("Pasante bloqueado correctamente", HttpStatus.NO_CONTENT);
            } else {
                throw new DataConflictException("Pasante se encuentra desactivado, bloqueado o eliminado");
            }
        } else {
            throw new ResourceNotFoundException("Pasante con id " + id + " no encontrado");
        }
    }

    // Obtener pasantes por filtro
    public ResponseEntity<Page<PasanteDTO>> findPasantesFiltro(String filtro, Pageable pageable) {
        logger.info("findPasantesFiltro()");
        logger.info("Obteniendo pasantes con filtro: " + filtro);
        Page<PasanteEntity> pasantes = pasanteRepo.findPasantesFiltro(filtro, pageable);
        Page<PasanteDTO> pasantesDTOs = pasantes.map(pasante -> mapearDTO(pasante));
        logger.info("200 OK: Pasantes con filtro " + filtro + " obtenidos correctamente");
        return new ResponseEntity<>(pasantesDTOs, HttpStatus.OK);
    }
}
