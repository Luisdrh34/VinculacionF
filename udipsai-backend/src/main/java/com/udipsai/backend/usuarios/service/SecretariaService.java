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
import java.util.Set;
import java.util.stream.Collectors;

import com.udipsai.backend.usuarios.persistence.entity.SecretariaEntity;
import com.udipsai.backend.usuarios.persistence.entity.RolEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioAreaEntity;
import com.udipsai.backend.common.exception.DataConflictException;
import com.udipsai.backend.common.exception.InvalidRequestBodyException;
import com.udipsai.backend.common.exception.ResourceNotFoundException;
import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolEntity;
import com.udipsai.backend.usuarios.persistence.repository.AreaRepository;
import com.udipsai.backend.usuarios.persistence.repository.SecretariaRepository;
import com.udipsai.backend.usuarios.persistence.repository.RolRepository;
import com.udipsai.backend.usuarios.persistence.repository.UsuarioRepository;
import com.udipsai.backend.usuarios.service.dto.SecretariaDTO;

import com.udipsai.backend.usuarios.service.dto.RegistrarSecretariaDTO;
import com.udipsai.backend.usuarios.service.dto.RolDTO;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;
import org.springframework.transaction.annotation.Transactional;

/*
 * Servicio para las Secretarias.
*/
@Service
public class SecretariaService {
    @Autowired
    private SecretariaRepository secretariaRepo;

    @Autowired
    private UsuarioService usuarioServ;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private AreaRepository areaRepo;

    private static final Logger logger = LoggerFactory.getLogger(SecretariaService.class);

    // Mapear de Secretaria a DTO.
    public SecretariaDTO mapearDTO(SecretariaEntity sec) {
        SecretariaDTO dto = new SecretariaDTO();
        UsuarioEntity usuario = sec.getUsuario();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setIdSecretaria(sec.getIdSecretaria());
        dto.setCedula(usuario.getCedula());
        dto.setEstado(usuario.getEstado());
        dto.setSecEstado(sec.getEstado());
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

    // Obtener las Secretarias activas.
    public ResponseEntity<Page<SecretariaDTO>> obtenerSecretarias(Pageable pageable) {
        logger.info("obtenerSecretarias()");
        logger.info("Obteniendo secretarias activas");
        Page<SecretariaEntity> secretariasA = secretariaRepo.findAllSecretarias(pageable);
        Page<SecretariaDTO> secretariasDTOs = secretariasA.map(sec -> mapearDTO(sec));
        logger.info("200 OK: Secretarias activas obtenidas correctamente");
        return new ResponseEntity<>(secretariasDTOs, HttpStatus.OK);
    }

    // Obtener una Secretaria especifica.
    public ResponseEntity<SecretariaDTO> obtenerSecretaria(String cedula) {
        logger.info("obtenerSecretaria()");
        logger.info("Obteniendo secretaria con cedula: " + cedula);
        SecretariaEntity secretariaEncontrada = secretariaRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Secretaria con cedula " + cedula + " no encontrada"));
        SecretariaDTO secretariaDTO = mapearDTO(secretariaEncontrada);
        logger.info("200 OK: Secretaria obtenida correctamente");
        return new ResponseEntity<>(secretariaDTO, HttpStatus.OK);
    }

    // Obtener todas las Secretarias (A, I, B, N).
    public ResponseEntity<Page<SecretariaDTO>> obtenerSecretariasTodas(Pageable pageable) {
        logger.info("obtenerSecretariasTodas()");
        logger.info("Obteniendo todas las secretarias");
        Page<SecretariaEntity> secretarias = secretariaRepo.findAll(pageable);
        Page<SecretariaDTO> secretariasDTOs = secretarias.map(sec -> mapearDTO(sec));
        logger.info("200 OK: Todas las secretarias obtenidas correctamente");
        return new ResponseEntity<>(secretariasDTOs, HttpStatus.OK);
    }

    // Obtener las Secretarias con estado especifico.
    public ResponseEntity<Page<SecretariaDTO>> obtenerSecretariasEstado(String estado, Pageable pageable) {
        logger.info("obtenerSecretariasEstado()");
        logger.info("Obteniendo secretarias con estado {}", estado);
        Page<SecretariaEntity> secretariasE = secretariaRepo.findAllByEstado(estado, pageable);
        Page<SecretariaDTO> secretariasDTOs = secretariasE.map(sec -> mapearDTO(sec));
        logger.info("200 OK: Secretarias con estado {} obtenidas correctamente", estado);
        return new ResponseEntity<>(secretariasDTOs, HttpStatus.OK);
    }

    // Registrar Secretaria.
    @Transactional
    public ResponseEntity<SecretariaDTO> registrarSecretaria(RegistrarSecretariaDTO usuario) {
        logger.info("registrarSecretaria()");
        logger.info("Registrando secretaria con cedula: " + usuario.getCedula());

        if (usuario.getCedula() == null || usuario.getContrasenia() == null || usuario.getNombres() == null
                || usuario.getApellidos() == null
                || usuario.getEmail() == null || usuario.getCelular() == null || usuario.getRoles() == null
                || usuario.getAreas() == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para el registro");
        }

        SecretariaEntity secretariaNueva = new SecretariaEntity();
        secretariaNueva.setEstado("A");

        if (usuarioRepo.existsByCedula(usuario.getCedula())) {
            UsuarioEntity usuarioExistente = usuarioRepo.findByCedula(usuario.getCedula()).get();

            if (usuarioExistente.getEstado().equals("A")) {
                if (secretariaRepo.existsByUsuario_Cedula(usuario.getCedula())) {
                    throw new DataConflictException("Ya existe una secretaria con cedula " + usuario.getCedula());
                } else {
                    RolEntity rolSecretaria = rolRepo.findByNombre("SECRETARIA").get();

                    if (rolSecretaria != null && rolSecretaria.getEstado().equals("A")) {
                        UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
                        usuarioRol.setUsuario(usuarioExistente);
                        usuarioRol.setRol(rolSecretaria);

                        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
                        usuarioRol.setFechaAsignacion(localDateTimeUTC5);

                        usuarioRol.setEstado("A");
                        usuarioExistente.getUsuarioRoles().add(usuarioRol);
                        usuarioRepo.save(usuarioExistente);
                        secretariaNueva.setUsuario(usuarioExistente);
                    } else {
                        throw new DataConflictException("Rol SECRETARIA no esta activo");
                    }
                }
            } else {
                throw new DataConflictException(
                        "Usuario ya existe con cedula " + usuario.getCedula() + " y estÃ¡ inactivo");
            }
        } else {
            RolEntity rolSecretaria = rolRepo.findByNombre("SECRETARIA").get();
            UsuarioEntity usuarioNuevo = usuarioServ.registrarUsuario(usuario);
            UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
            usuarioRol.setUsuario(usuarioNuevo);
            usuarioRol.setRol(rolSecretaria);

            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioRol.setFechaAsignacion(localDateTimeUTC5);
            usuarioRol.setEstado("A");

            Set<UsuarioRolEntity> usuarioRoles = new HashSet<>();
            usuarioRoles.add(usuarioRol);
            usuarioNuevo.setUsuarioRoles(usuarioRoles);
            usuarioRepo.save(usuarioNuevo);
            secretariaNueva.setUsuario(usuarioNuevo);
            secretariaNueva.setUsuario(usuarioNuevo);
        }

        SecretariaEntity secretariaGuardada = secretariaRepo.save(secretariaNueva);
        SecretariaDTO secretariaDTO = mapearDTO(secretariaGuardada);

        logger.info("201 CREATED: Secretaria registrada con datos: ");
        logger.info("Cedula: " + secretariaGuardada.getUsuario().getCedula());
        logger.info("Nombres: " + secretariaGuardada.getUsuario().getNombres());
        logger.info("Apellidos: " + secretariaGuardada.getUsuario().getApellidos());
        logger.info("Email: " + secretariaGuardada.getUsuario().getEmail());
        logger.info("Celular: " + secretariaGuardada.getUsuario().getCelular());
        logger.info("Fecha de creacion: " + secretariaGuardada.getUsuario().getFechaCreacion().toString());
        return new ResponseEntity<>(secretariaDTO, HttpStatus.CREATED);
    }

    // Actualizar Secretaria.
    public ResponseEntity<SecretariaDTO> actualizarSecretaria(String cedula, SecretariaDTO secretaria) {
        logger.info("actualizarSecretaria()");
        logger.info("Actualizando secretaria con cedula: " + cedula);
        SecretariaEntity secretariaEncontrada = secretariaRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Secretaria con cedula " + cedula + " no encontrada"));

        if (secretaria.getNombres() == null || secretaria.getApellidos() == null || secretaria.getEmail() == null
                || secretaria.getCelular() == null || secretaria.getAreas() == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para la actualizacion de datos");
        }

        UsuarioEntity usuarioEncontrado = secretariaEncontrada.getUsuario();
        usuarioEncontrado.setNombres(secretaria.getNombres());
        usuarioEncontrado.setApellidos(secretaria.getApellidos());
        usuarioEncontrado.setEmail(secretaria.getEmail());
        usuarioEncontrado.setCelular(secretaria.getCelular());

        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
        usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);

        usuarioEncontrado.getUsuarioAreas().forEach(usuarioArea -> {
            usuarioArea.setEstado("N");
        });

        Set<UsuarioAreaEntity> usuarioAreas = secretaria.getAreas().stream()
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
        secretariaEncontrada.setUsuario(usuarioActualizado);
        SecretariaEntity secretariaActualizada = secretariaRepo.save(secretariaEncontrada);
        SecretariaDTO secretariaDTO = mapearDTO(secretariaActualizada);
        logger.info("200 OK: Secretaria actualizada con datos:");
        logger.info("Cedula: " + secretariaActualizada.getUsuario().getCedula());
        logger.info("Nombres: " + secretariaActualizada.getUsuario().getNombres());
        logger.info("Apellidos: " + secretariaActualizada.getUsuario().getApellidos());
        logger.info("Email: " + secretariaActualizada.getUsuario().getEmail());
        logger.info("Celular: " + secretariaActualizada.getUsuario().getCelular());
        logger.info(
                "Fecha de modificacion: "
                        + secretariaActualizada.getUsuario().getFechaModificacion().toString());
        return new ResponseEntity<>(secretariaDTO, HttpStatus.OK);
    }

    // Eliminar un Secretaria.
    public ResponseEntity<String> eliminarSecretaria(String cedula) {
        logger.info("eliminarSecretaria()");
        logger.info("Eliminando secretaria con cedula: " + cedula);
        SecretariaEntity secretariaEncontrada = secretariaRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Secretaria con cedula " + cedula + " no encontrada"));
        UsuarioEntity usuarioEncontrado = secretariaEncontrada.getUsuario();

        if (usuarioEncontrado.getUsuarioRoles().stream()
                .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("SECRETARIA"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("N");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getRol().getNombre().equals("SECRETARIA")) {
                usuarioRol.setEstado("N");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);
        secretariaEncontrada.setUsuario(usuarioActualizado);
        secretariaEncontrada.setEstado("N");
        secretariaRepo.save(secretariaEncontrada);
        logger.info("204 NO CONTENT: Secretaria eliminada correctamente");
        return new ResponseEntity<>("Secretaria eliminada correctamente", HttpStatus.NO_CONTENT);
    }

    // Activar un Secretaria.
    public ResponseEntity<String> activarSecretaria(String cedula) {
        logger.info("activarSecretaria()");
        logger.info("Activando secretaria con cedula: " + cedula);
        SecretariaEntity secretariaEncontrada = secretariaRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Secretaria con cedula " + cedula + " no encontrada"));

        UsuarioEntity usuarioEncontrado = secretariaEncontrada.getUsuario();
        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (!usuarioRol.getEstado().equals("N") && usuarioRol.getRol().getNombre().equals("SECRETARIA")) {
                usuarioRol.setEstado("A");
            }
        });

        if (!usuarioEncontrado.getEstado().equals("N")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("SECRETARIA"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("A");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (!secretariaEncontrada.getEstado().equals("N")) {
            secretariaEncontrada.setUsuario(usuarioActualizado);
            secretariaEncontrada.setEstado("A");
            secretariaRepo.save(secretariaEncontrada);
            logger.info("204 NO CONTENT: Secretaria activada correctamente");
            return new ResponseEntity<>("Secretaria activada correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Secretaria se encuentra eliminada");
        }
    }

    // Desactivar un Secretaria.
    public ResponseEntity<String> desactivarSecretaria(String cedula) {
        logger.info("desactivarSecretaria()");
        logger.info("Desactivando secretaria con cedula: " + cedula);
        SecretariaEntity secretariaEncontrada = secretariaRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Secretaria con cedula " + cedula + " no encontrada"));
        UsuarioEntity usuarioEncontrado = secretariaEncontrada.getUsuario();

        if (usuarioEncontrado.getEstado().equals("A")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("SECRETARIA"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("I");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("SECRETARIA")) {
                usuarioRol.setEstado("I");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (secretariaEncontrada.getEstado().equals("A")) {
            secretariaEncontrada.setUsuario(usuarioActualizado);
            secretariaEncontrada.setEstado("I");
            secretariaRepo.save(secretariaEncontrada);
            logger.info("204 NO CONTENT: Secretaria desactivada correctamente");
            return new ResponseEntity<>("Secretaria desactivada correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Secretaria se encuentra desactivada, bloqueada o eliminada");
        }
    }

    // Bloquear un Secretaria.
    public ResponseEntity<String> bloquearSecretaria(String cedula) {
        logger.info("bloquearSecretaria()");
        logger.info("Bloqueando secretaria con cedula: " + cedula);
        SecretariaEntity secretariaEncontrada = secretariaRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Secretaria con cedula " + cedula + " no encontrada"));
        UsuarioEntity usuarioEncontrado = secretariaEncontrada.getUsuario();

        if (usuarioEncontrado.getEstado().equals("A")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("SECRETARIA"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("B");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("SECRETARIA")) {
                usuarioRol.setEstado("B");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (secretariaEncontrada.getEstado().equals("A")) {
            secretariaEncontrada.setUsuario(usuarioActualizado);
            secretariaEncontrada.setEstado("B");
            secretariaRepo.save(secretariaEncontrada);
            logger.info("204 NO CONTENT: Secretaria bloqueada correctamente");
            return new ResponseEntity<>("Secretaria bloqueada correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Secretaria se encuentra desactivada, bloqueada o eliminada");
        }
    }

    // Obtener Secretarias por filtro
    public ResponseEntity<Page<SecretariaDTO>> obtenerSecretariasFiltro(String filtro, Pageable pageable) {
        logger.info("obtenerSecretariasFiltro()");
        logger.info("Obteniendo secretarias con filtro: " + filtro);
        Page<SecretariaEntity> secretarias = secretariaRepo.findSecretariasFiltro(filtro, pageable);
        Page<SecretariaDTO> secretariasDTOs = secretarias.map(sec -> mapearDTO(sec));
        logger.info("200 OK: Secretarias con filtro obtenidas correctamente");
        return new ResponseEntity<>(secretariasDTOs, HttpStatus.OK);
    }
}
