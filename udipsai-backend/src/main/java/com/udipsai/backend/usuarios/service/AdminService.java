package com.udipsai.backend.usuarios.service;

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
import java.util.Set;
import java.util.stream.Collectors;

import com.udipsai.backend.common.service.UsuarioService;
import com.udipsai.backend.common.exception.DataConflictException;
import com.udipsai.backend.common.exception.InvalidRequestBodyException;
import com.udipsai.backend.common.exception.ResourceNotFoundException;
import com.udipsai.backend.usuarios.persistence.entity.AdminEntity;
import com.udipsai.backend.usuarios.persistence.entity.RolEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioAreaEntity;
import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolEntity;
import com.udipsai.backend.usuarios.persistence.repository.AreaRepository;
import com.udipsai.backend.usuarios.persistence.repository.AdminRepository;
import com.udipsai.backend.usuarios.persistence.repository.RolRepository;
import com.udipsai.backend.usuarios.persistence.repository.UsuarioRepository;
import com.udipsai.backend.usuarios.service.dto.AdminDTO;
import com.udipsai.backend.usuarios.service.dto.RegistrarAdminDTO;
import com.udipsai.backend.usuarios.service.dto.RolDTO;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;

/*
 * Servicio para los Admins.
*/
@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private UsuarioService usuarioServ;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private AreaRepository areaRepo;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    // Mapear de un Admin a DTO.
    public AdminDTO mapearDTO(AdminEntity adm) {
        AdminDTO dto = new AdminDTO();
        UsuarioEntity usuario = adm.getUsuario();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setIdAdmin(adm.getIdAdmin());
        dto.setCedula(usuario.getCedula());
        dto.setEstado(usuario.getEstado());
        dto.setAdmEstado(adm.getEstado());
        dto.setNombres(usuario.getNombres());
        dto.setApellidos(usuario.getApellidos());
        dto.setEmail(usuario.getEmail());
        dto.setCelular(usuario.getCelular());
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
        return dto;
    }

    // Obtener los Admins activos.
    public ResponseEntity<Page<AdminDTO>> obtenerAdmins(Pageable pageable) {
        logger.info("obtenerAdmins()");
        logger.info("Obteniendo admins activos");
        Page<AdminEntity> adminsA = adminRepo.findAllByEstado("A", pageable);
        Page<AdminDTO> adminsDTOs = adminsA.map(adm -> mapearDTO(adm));
        logger.info("200 OK: Admins activos obtenidos correctamente");
        return new ResponseEntity<>(adminsDTOs, HttpStatus.OK);
    }

    // Obtener un Admin especifico.
    public ResponseEntity<AdminDTO> obtenerAdmin(String cedula) {
        logger.info("obtenerAdmin()");
        logger.info("Obteniendo admin con cedula: " + cedula);
        AdminEntity adminEncontrado = adminRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Admin con cedula " + cedula + " no encontrado"));

        AdminDTO adminDTO = mapearDTO(adminEncontrado);
        logger.info("200 OK: Admin obtenido correctamente");
        return new ResponseEntity<>(adminDTO, HttpStatus.OK);
    }

    // Obtener todos los Admins (A, I, B, N).
    public ResponseEntity<Page<AdminDTO>> obtenerAdminsTodos(Pageable pageable) {
        logger.info("obtenerAdminsTodos()");
        logger.info("Obteniendo todos los admins");
        Page<AdminEntity> admins = adminRepo.findAll(pageable);
        Page<AdminDTO> adminsDTOs = admins.map(admin -> mapearDTO(admin));
        logger.info("200 OK: Todos los admins obtenidos correctamente");
        return new ResponseEntity<>(adminsDTOs, HttpStatus.OK);
    }

    // Obtener los Admins con estado especifico.
    public ResponseEntity<Page<AdminDTO>> obtenerAdminsEstado(String estado, Pageable pageable) {
        logger.info("obtenerAdminsEstado()");
        logger.info("Obteniendo admins con estado {}", estado);
        Page<AdminEntity> adminsE = adminRepo.findAllByEstado(estado, pageable);
        Page<AdminDTO> adminsDTOs = adminsE.map(admin -> mapearDTO(admin));
        logger.info("200 OK: Admins con estado {} obtenidos correctamente", estado);
        return new ResponseEntity<>(adminsDTOs, HttpStatus.OK);
    }

    // Registrar un Admin.
    public ResponseEntity<AdminDTO> registrarAdmin(RegistrarAdminDTO usuario) {
        logger.info("registrarAdmin()");
        logger.info("Registrando admin con cedula: " + usuario.getCedula());

        if (usuario.getCedula() == null || usuario.getContrasenia() == null || usuario.getNombres() == null
                || usuario.getApellidos() == null
                || usuario.getEmail() == null || usuario.getCelular() == null || usuario.getRoles() == null
                || usuario.getAreas() == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para el registro");
        }

        if (adminRepo.findAll().size() < 4) {
            AdminEntity adminNuevo = new AdminEntity();
            adminNuevo.setEstado("A");

            if (usuarioRepo.existsByCedula(usuario.getCedula())) {
                UsuarioEntity usuarioExistente = usuarioRepo.findByCedula(usuario.getCedula()).get();

                if (usuarioExistente.getEstado().equals("A")) {
                    if (adminRepo.existsByUsuario_Cedula(usuario.getCedula())) {
                        throw new DataConflictException(
                                "409 CONFLICT: Admin ya existe con cedula " + usuario.getCedula() + " o email "
                                        + usuarioExistente.getEmail());
                    } else {
                        RolEntity rolProfesional = rolRepo.findByNombre("ADMIN").get();

                        if (rolProfesional != null && rolProfesional.getEstado().equals("A")) {
                            UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
                            usuarioRol.setUsuario(usuarioExistente);
                            usuarioRol.setRol(rolProfesional);

                            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
                            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
                            usuarioRol.setFechaAsignacion(localDateTimeUTC5);
                            usuarioRol.setEstado("A");

                            usuarioExistente.getUsuarioRoles().add(usuarioRol);
                            usuarioRepo.save(usuarioExistente);

                            adminNuevo.setUsuario(usuarioExistente);
                        } else {
                            throw new DataConflictException("Rol ADMIN no existe o está inactivo");
                        }
                    }
                } else {
                    throw new DataConflictException("Usuario ya existe con cedula "
                            + usuario.getCedula() + " y está inactivo");
                }
            } else {
                UsuarioEntity usuarioNuevo = usuarioServ.registrarUsuario(usuario);
                adminNuevo.setUsuario(usuarioNuevo);
            }

            AdminEntity adminGuardado = adminRepo.save(adminNuevo);
            AdminDTO adminDTO = mapearDTO(adminGuardado);

            logger.info("201 CREATED: Admin registrado con datos: ");
            logger.info("Cedula: " + adminGuardado.getUsuario().getCedula());
            logger.info("Nombres: " + adminGuardado.getUsuario().getNombres());
            logger.info("Apellidos: " + adminGuardado.getUsuario().getApellidos());
            logger.info("Email: " + adminGuardado.getUsuario().getEmail());
            logger.info("Celular: " + adminGuardado.getUsuario().getCelular());
            logger.info("Fecha de creacion: " + adminGuardado.getUsuario().getFechaCreacion().toString());

            return new ResponseEntity<>(adminDTO, HttpStatus.CREATED);
        } else {
            throw new DataConflictException("Ya existen 4 administradores registrados");
        }
    }

    // Actualizar un Admin.
    public ResponseEntity<AdminDTO> actualizarAdmin(String cedula, AdminDTO admin) {
        logger.info("actualizarAdmin()");
        logger.info("Actualizando admin con cedula: " + cedula);

        AdminEntity adminEncontrado = adminRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Admin con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = adminEncontrado.getUsuario();

        usuarioEncontrado.setNombres(admin.getNombres());
        usuarioEncontrado.setApellidos(admin.getApellidos());
        usuarioEncontrado.setEmail(admin.getEmail());
        usuarioEncontrado.setCelular(admin.getCelular());

        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
        usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);

        usuarioEncontrado.getUsuarioAreas().forEach(usuarioArea -> {
            usuarioArea.setEstado("N");
        });

        Set<UsuarioAreaEntity> usuarioAreas = admin.getAreas().stream()
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
        adminEncontrado.setUsuario(usuarioActualizado);
        AdminEntity adminActualizado = adminRepo.save(adminEncontrado);
        AdminDTO adminDTO = mapearDTO(adminActualizado);

        logger.info("200 OK: Admin actualizado con datos:");
        logger.info("Cedula: " + adminActualizado.getUsuario().getCedula());
        logger.info("Nombres: " + adminActualizado.getUsuario().getNombres());
        logger.info("Apellidos: " + adminActualizado.getUsuario().getApellidos());
        logger.info("Email: " + adminActualizado.getUsuario().getEmail());
        logger.info("Celular: " + adminActualizado.getUsuario().getCelular());
        logger.info(
                "Fecha de modificacion: "
                        + adminActualizado.getUsuario().getFechaModificacion().toString());

        return new ResponseEntity<>(adminDTO, HttpStatus.OK);
    }

    // Eliminar un Admin.
    public ResponseEntity<String> eliminarAdmin(String cedula) {
        logger.info("eliminarAdmin()");
        logger.info("Eliminando admin con cedula: " + cedula);

        AdminEntity adminEncontrado = adminRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Admin con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = adminEncontrado.getUsuario();

        if (usuarioEncontrado.getUsuarioRoles().stream()
                .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("ADMIN"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("N");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getRol().getNombre().equals("ADMIN")) {
                usuarioRol.setEstado("N");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        adminEncontrado.setUsuario(usuarioActualizado);
        adminEncontrado.setEstado("N");
        adminRepo.save(adminEncontrado);
        logger.info("204 NO CONTENT: Admin eliminado correctamente");

        return new ResponseEntity<>("Admin eliminado correctamente", HttpStatus.NO_CONTENT);
    }

    // Activar un Admin.
    public ResponseEntity<String> activarAdmin(String cedula) {
        logger.info("activarAdmin()");
        logger.info("Activando admin con cedula: " + cedula);

        AdminEntity adminEncontrado = adminRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Admin con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = adminEncontrado.getUsuario();

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (!usuarioRol.getEstado().equals("N") && usuarioRol.getRol().getNombre().equals("ADMIN")) {
                usuarioRol.setEstado("A");
            }
        });

        if (!usuarioEncontrado.getEstado().equals("N")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("ADMIN"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("A");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (!adminEncontrado.getEstado().equals("N")) {
            adminEncontrado.setUsuario(usuarioActualizado);
            adminEncontrado.setEstado("A");
            adminRepo.save(adminEncontrado);
            logger.info("204 NO CONTENT: Admin activado correctamente");

            return new ResponseEntity<>("Admin activado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Admin se encuentra eliminado");
        }
    }

    // Desactivar un Admin.
    public ResponseEntity<String> desactivarAdmin(String cedula) {
        logger.info("desactivarAdmin()");
        logger.info("Desactivando admin con cedula: " + cedula);

        AdminEntity adminEncontrado = adminRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Admin con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = adminEncontrado.getUsuario();

        if (usuarioEncontrado.getEstado().equals("A")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("ADMIN"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("I");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("ADMIN")) {
                usuarioRol.setEstado("I");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (adminEncontrado.getEstado().equals("A")) {
            adminEncontrado.setUsuario(usuarioActualizado);
            adminEncontrado.setEstado("I");
            adminRepo.save(adminEncontrado);
            logger.info("204 NO CONTENT: Admin desactivado correctamente");

            return new ResponseEntity<>("Admin desactivado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Admin fue desactivado, bloqueado o eliminado");
        }
    }

    // Bloquear un Admin.
    public ResponseEntity<String> bloquearAdmin(String cedula) {
        logger.info("bloquearAdmin()");
        logger.info("Bloqueando admin con cedula: " + cedula);

        AdminEntity adminEncontrado = adminRepo.findByUsuario_Cedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Admin con cedula " + cedula + " no encontrado"));

        UsuarioEntity usuarioEncontrado = adminEncontrado.getUsuario();

        if (usuarioEncontrado.getEstado().equals("A")
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")).toList().size() == 1
                && usuarioEncontrado.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A")
                                && usuarioRol.getRol().getNombre().equals("ADMIN"))
                        .toList().size() == 1) {
            usuarioEncontrado.setEstado("B");
            ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
            usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);
        }

        usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> {
            if (usuarioRol.getEstado().equals("A") && usuarioRol.getRol().getNombre().equals("ADMIN")) {
                usuarioRol.setEstado("B");
            }
        });

        UsuarioEntity usuarioActualizado = usuarioRepo.save(usuarioEncontrado);

        if (adminEncontrado.getEstado().equals("A")) {
            adminEncontrado.setUsuario(usuarioActualizado);
            adminEncontrado.setEstado("B");
            adminRepo.save(adminEncontrado);
            logger.info("204 NO CONTENT: Admin bloqueado correctamente");

            return new ResponseEntity<>("Admin bloqueado correctamente", HttpStatus.NO_CONTENT);
        } else {
            throw new DataConflictException("Admin fue desactivado, bloqueado o eliminado");
        }
    }
}
