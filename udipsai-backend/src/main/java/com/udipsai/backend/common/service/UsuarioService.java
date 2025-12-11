package com.udipsai.backend.common.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.udipsai.backend.common.exception.InvalidRequestBodyException;
import com.udipsai.backend.common.exception.ResourceNotFoundException;
import com.udipsai.backend.common.exception.UnauthorizedAccessException;
import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;
import com.udipsai.backend.usuarios.persistence.entity.RolEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioAreaEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolEntity;
import com.udipsai.backend.usuarios.persistence.repository.AreaRepository;
import com.udipsai.backend.usuarios.persistence.repository.RolRepository;
import com.udipsai.backend.usuarios.persistence.repository.UsuarioRepository;
import com.udipsai.backend.usuarios.service.dto.UsuarioDTO;
import com.udipsai.backend.usuarios.service.dto.RolDTO;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;
import com.udipsai.backend.usuarios.service.dto.CambiarContraseniaDTO;
import com.udipsai.backend.auth.service.dto.RegisterDto;
import com.udipsai.backend.auth.service.UsuarioRolService;

/**
 * Servicio unificado para gestión completa de Usuarios.
 * Combina funcionalidades de autenticación y gestión de usuarios.
 */
@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private AreaRepository areaRepo;

    @Autowired
    private UsuarioRolService usuarioRolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    // ==================== MÉTODOS DE AUTENTICACIÓN (Auth Module) ====================

    /**
     * Guardar usuario para registro inicial (usado en autenticación)
     */
    @Transactional
    public UsuarioEntity saveUsuario(RegisterDto usuarioRegister) {
        logger.info("saveUsuario()");
        logger.info("Guardando usuario con cédula: " + usuarioRegister.getCedula());
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setCedula(usuarioRegister.getCedula());
        usuario.setEmail(usuarioRegister.getEmail());
        usuario.setEstado("A");
        usuario.setContrasenia(passwordEncoder.encode(usuarioRegister.getContrasenia()));

        UsuarioEntity savedUsuario = usuarioRepo.save(usuario);

        if (savedUsuario.getCedula() != null) {
            UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
            usuarioRol.setUsuario(savedUsuario);

            RolEntity rolEntity = mapRoleToRolEntity(usuarioRegister.getRol());
            usuarioRol.setRol(rolEntity);

            usuarioRol.setFechaAsignacion(LocalDateTime.now());
            usuarioRol.setEstado("A");

            usuarioRolService.createUsuarioRol(usuarioRol);
        }

        logger.info("Usuario guardado con datos:");
        logger.info("Cedula: " + savedUsuario.getCedula());
        logger.info("Email: " + savedUsuario.getEmail());
        logger.info("Estado: " + savedUsuario.getEstado());

        return savedUsuario;
    }

    public Optional<UsuarioEntity> findByCedula(String cedula) {
        return usuarioRepo.findByCedula(cedula);
    }

    public List<UsuarioEntity> findAllUsuarios() {
        return (List<UsuarioEntity>) usuarioRepo.findAll();
    }

    public List<String> getUsuarioRoles(String cedula) {
        Optional<UsuarioEntity> usuarioOptional = usuarioRepo.findByCedula(cedula);
        if (usuarioOptional.isPresent()) {
            UsuarioEntity usuario = usuarioOptional.get();
            return usuario.getUsuarioRoles().stream()
                    .map(usuarioRol -> usuarioRol.getRol().getNombre())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    @Transactional
    public void deleteUsuarioByCedula(String cedula) {
        usuarioRepo.deleteByCedula(cedula);
    }

    @Transactional
    public void updateUsuario(UsuarioEntity updatedUsuario) {
        if (!usuarioRepo.existsByCedula(updatedUsuario.getCedula())) {
            throw new EntityNotFoundException("No se encontró un usuario con la cédula proporcionada.");
        }
        usuarioRepo.save(updatedUsuario);
    }

    private RolEntity mapRoleToRolEntity(RegisterDto.Role role) {
        RolEntity rolEntity = new RolEntity();
        if (role != null) {
            switch (role) {
                case ADMIN:
                    rolEntity.setIdRol(2L);
                    rolEntity.setNombre("ADMIN");
                    break;
                case USER:
                default:
                    rolEntity.setIdRol(1L);
                    rolEntity.setNombre("USER");
                    break;
            }
        } else {
            rolEntity.setIdRol(1L);
            rolEntity.setNombre("USER");
        }
        return rolEntity;
    }

    // ==================== MÉTODOS DE GESTIÓN (Usuarios Module) ====================

    /**
     * Mapear de un Usuario a DTO
     */
    public UsuarioDTO mapearDTO(UsuarioEntity usr) {
        UsuarioDTO dto = new UsuarioDTO(
                usr.getIdUsuario(),
                usr.getCedula(),
                usr.getEstado(),
                usr.getNombres(),
                usr.getApellidos(),
                usr.getEmail(),
                usr.getCelular(),
                usr.getUsuarioRoles().stream()
                        .filter(usuarioRol -> usuarioRol.getEstado().equals("A"))
                        .map(usuarioRol -> {
                            RolEntity rol = usuarioRol.getRol();
                            RolDTO rolDTO = new RolDTO(
                                    rol.getIdRol(),
                                    rol.getNombre(),
                                    rol.getEstado());
                            return rolDTO;
                        }).collect(Collectors.toSet()),
                usr.getUsuarioAreas().stream()
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

    /**
     * Mapear de una lista de Usuarios a una lista de DTOs
     */
    public List<UsuarioDTO> mapearDTOs(List<UsuarioEntity> usuarios) {
        List<UsuarioDTO> dtos = usuarios
                .stream()
                .map(usr -> mapearDTO(usr))
                .toList();
        return dtos;
    }

    /**
     * Obtener todos los Usuarios activos
     */
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuarios(Pageable pageable) {
        logger.info("obtenerUsuariosActivos()");
        logger.info("Obteniendo todos los usuarios activos");
        Page<UsuarioEntity> usuariosActivosPage = usuarioRepo.findAllByEstado("A", pageable);
        Page<UsuarioDTO> usuariosDTOs = usuariosActivosPage.map(usr -> mapearDTO(usr));
        logger.info("200 OK: Usuarios activos obtenidos correctamente");
        return new ResponseEntity<>(usuariosDTOs, HttpStatus.OK);
    }

    /**
     * Obtener un Usuario especifico
     */
    public ResponseEntity<UsuarioDTO> obtenerUsuario(String cedula) {
        logger.info("obtenerUsuario()");
        logger.info("Obteniendo usuario con cedula: " + cedula);
        UsuarioEntity usuario = usuarioRepo.findByCedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Usuario con cedula " + cedula + " no encontrado"));
        UsuarioDTO usuarioDTO = mapearDTO(usuario);
        logger.info("200 OK: Usuario obtenido correctamente");
        return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
    }

    /**
     * Obtener todos los Usuarios (A, I, B, N)
     */
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosTodos(Pageable pageable) {
        logger.info("obtenerUsuarios()");
        logger.info("Obteniendo todos los usuarios");
        Page<UsuarioEntity> usuariosPage = usuarioRepo.findAll(pageable);
        Page<UsuarioDTO> usuariosDTOs = usuariosPage.map(usr -> mapearDTO(usr));
        logger.info("200 OK: Usuarios obtenidos correctamente");
        return new ResponseEntity<>(usuariosDTOs, HttpStatus.OK);
    }

    /**
     * Obtener todos los Usuarios inactivos
     */
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosInactivos(Pageable pageable) {
        logger.info("obtenerUsuariosInactivos()");
        logger.info("Obteniendo todos los usuarios inactivos");
        Page<UsuarioEntity> usuariosInactivosPage = usuarioRepo.findAllByEstado("I", pageable);
        Page<UsuarioDTO> usuariosDTOs = usuariosInactivosPage.map(usr -> mapearDTO(usr));
        logger.info("200 OK: Usuarios inactivos obtenidos correctamente");
        return new ResponseEntity<>(usuariosDTOs, HttpStatus.OK);
    }

    /**
     * Obtener todos los Usuarios bloqueados
     */
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosBloqueados(Pageable pageable) {
        logger.info("obtenerUsuariosBloqueados()");
        logger.info("Obteniendo todos los usuarios bloqueados");
        Page<UsuarioEntity> usuariosBloqueadosPage = usuarioRepo.findAllByEstado("B", pageable);
        Page<UsuarioDTO> usuariosDTOs = usuariosBloqueadosPage.map(usr -> mapearDTO(usr));
        logger.info("200 OK: Usuarios bloqueados obtenidos correctamente");
        return new ResponseEntity<>(usuariosDTOs, HttpStatus.OK);
    }

    /**
     * Obtener todos los Usuarios eliminados
     */
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosEliminados(Pageable pageable) {
        logger.info("obtenerUsuariosEliminados()");
        logger.info("Obteniendo todos los usuarios eliminados");
        Page<UsuarioEntity> usuariosEliminadosPage = usuarioRepo.findAllByEstado("N", pageable);
        Page<UsuarioDTO> usuariosDTOs = usuariosEliminadosPage.map(usr -> mapearDTO(usr));
        logger.info("200 OK: Usuarios eliminados obtenidos correctamente");
        return new ResponseEntity<>(usuariosDTOs, HttpStatus.OK);
    }

    /**
     * Registrar un Usuario nuevo con DTOs específicos de cada tipo de usuario
     */
    public <T> UsuarioEntity registrarUsuario(T usuarioDTO) {
        logger.info("registrarUsuario()");
        
        // Extraer datos básicos mediante reflection o casting genérico
        String cedula = null;
        String contrasenia = null;
        String nombres = null;
        String apellidos = null;
        String email = null;
        String celular = null;
        Set<?> roles = null;
        Set<?> areas = null;

        try {
            java.lang.reflect.Method getCedula = usuarioDTO.getClass().getMethod("getCedula");
            java.lang.reflect.Method getContrasenia = usuarioDTO.getClass().getMethod("getContrasenia");
            java.lang.reflect.Method getNombres = usuarioDTO.getClass().getMethod("getNombres");
            java.lang.reflect.Method getApellidos = usuarioDTO.getClass().getMethod("getApellidos");
            java.lang.reflect.Method getEmail = usuarioDTO.getClass().getMethod("getEmail");
            java.lang.reflect.Method getCelular = usuarioDTO.getClass().getMethod("getCelular");
            java.lang.reflect.Method getRoles = usuarioDTO.getClass().getMethod("getRoles");
            java.lang.reflect.Method getAreas = usuarioDTO.getClass().getMethod("getAreas");

            cedula = (String) getCedula.invoke(usuarioDTO);
            contrasenia = (String) getContrasenia.invoke(usuarioDTO);
            nombres = (String) getNombres.invoke(usuarioDTO);
            apellidos = (String) getApellidos.invoke(usuarioDTO);
            email = (String) getEmail.invoke(usuarioDTO);
            celular = (String) getCelular.invoke(usuarioDTO);
            roles = (Set<?>) getRoles.invoke(usuarioDTO);
            areas = (Set<?>) getAreas.invoke(usuarioDTO);
        } catch (Exception e) {
            throw new InvalidRequestBodyException("Error al procesar los datos del usuario: " + e.getMessage());
        }

        logger.info("Registrando usuario con cedula {}", cedula);

        if (cedula == null || contrasenia == null || nombres == null
                || apellidos == null || email == null || celular == null) {
            throw new InvalidRequestBodyException("Faltan campos obligatorios para registrar el usuario");
        }

        UsuarioEntity usuarioNuevo = new UsuarioEntity();
        usuarioNuevo.setCedula(cedula);
        usuarioNuevo.setContrasenia(passwordEncoder.encode(contrasenia));
        usuarioNuevo.setNombres(nombres);
        usuarioNuevo.setApellidos(apellidos);
        usuarioNuevo.setEmail(email);
        usuarioNuevo.setCelular(celular);
        usuarioNuevo.setEstado("A");

        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
        usuarioNuevo.setFechaCreacion(localDateTimeUTC5);
        usuarioNuevo.setFechaModificacion(localDateTimeUTC5);

        Set<?> rolesSet = roles;
        Set<UsuarioRolEntity> usuarioRoles = rolesSet.stream().map(rol -> {
            try {
                java.lang.reflect.Method getNombre = rol.getClass().getMethod("getNombre");
                String nombreRol = (String) getNombre.invoke(rol);
                
                Optional<RolEntity> rolEncontrado = rolRepo.findByNombre(nombreRol);

                if (rolEncontrado.isPresent() && rolEncontrado.get().getEstado().equals("A")) {
                    UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
                    usuarioRol.setUsuario(usuarioNuevo);
                    usuarioRol.setRol(rolEncontrado.get());
                    usuarioRol.setFechaAsignacion(localDateTimeUTC5);
                    usuarioRol.setEstado("A");
                    return usuarioRol;
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());

        Set<?> areasSet = areas;
        Set<UsuarioAreaEntity> usuarioAreas = areasSet.stream().map(area -> {
            try {
                java.lang.reflect.Method getIdArea = area.getClass().getMethod("getIdArea");
                Long idArea = (Long) getIdArea.invoke(area);
                
                Optional<AreaEntity> areaEncontrada = areaRepo.findById(idArea);

                if (areaEncontrada.isPresent() && areaEncontrada.get().getEstado().equals("A")) {
                    UsuarioAreaEntity usuarioArea = new UsuarioAreaEntity();
                    usuarioArea.setUsuario(usuarioNuevo);
                    usuarioArea.setArea(areaEncontrada.get());
                    usuarioArea.setFechaAsignacion(localDateTimeUTC5);
                    usuarioArea.setEstado("A");
                    return usuarioArea;
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());

        usuarioNuevo.setUsuarioRoles(usuarioRoles);
        usuarioNuevo.setUsuarioAreas(usuarioAreas);

        UsuarioEntity usuarioGuardado = usuarioRepo.save(usuarioNuevo);
        logger.info("Usuario registrado correctamente");
        return usuarioGuardado;
    }

    /**
     * Cambiar contraseña de un Usuario
     */
    public ResponseEntity<?> cambiarContrasenia(String cedula, CambiarContraseniaDTO peticion) {
        logger.info("cambiarContrasenia()");
        logger.info("Cambiando contraseña de usuario con cedula: " + cedula);
        UsuarioEntity usuarioEncontrado = usuarioRepo.findByCedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Usuario con cedula " + cedula + " no encontrado"));

        String antigua = peticion.getContrasenia();
        String nueva = peticion.getNuevaContrasenia();

        if (!passwordEncoder.matches(antigua, usuarioEncontrado.getContrasenia())) {
            throw new UnauthorizedAccessException("Contraseña antigua es incorrecta");
        }

        usuarioEncontrado.setContrasenia(passwordEncoder.encode(nueva));

        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
        usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);

        usuarioRepo.save(usuarioEncontrado);
        logger.info("200 OK: Contraseña actualizada correctamente");
        logger.info("Fecha de modificacion: " + usuarioEncontrado.getFechaModificacion().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Contraseña actualizada correctamente");
        response.put("fechaModificacion", usuarioEncontrado.getFechaModificacion().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar un Usuario
     */
    public ResponseEntity<String> eliminarUsuario(String cedula) {
        logger.info("eliminarUsuario()");
        logger.info("Eliminando usuario con cedula: " + cedula);
        UsuarioEntity usuarioEncontrado = usuarioRepo.findByCedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Usuario con cedula " + cedula + " no encontrado"));
        usuarioEncontrado.setEstado("N");

        Set<UsuarioRolEntity> usuarioRoles = usuarioEncontrado.getUsuarioRoles();
        for (UsuarioRolEntity usuarioRol : usuarioRoles) {
            usuarioRol.setEstado("N");
        }

        Set<UsuarioAreaEntity> usuarioAreas = usuarioEncontrado.getUsuarioAreas();
        for (UsuarioAreaEntity usuarioArea : usuarioAreas) {
            usuarioArea.setEstado("N");
        }

        usuarioRepo.save(usuarioEncontrado);
        logger.info("200 OK: Usuario eliminado correctamente");
        return new ResponseEntity<>("Usuario eliminado correctamente", HttpStatus.OK);
    }

    /**
     * Habilitar un Usuario
     */
    public ResponseEntity<String> habilitarUsuario(String cedula) {
        logger.info("habilitarUsuario()");
        logger.info("Habilitando usuario con cedula: " + cedula);
        UsuarioEntity usuarioEncontrado = usuarioRepo.findByCedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Usuario con cedula " + cedula + " no encontrado"));
        usuarioEncontrado.setEstado("A");
        usuarioRepo.save(usuarioEncontrado);
        logger.info("200 OK: Usuario habilitado correctamente");
        return new ResponseEntity<>("Usuario activado correctamente", HttpStatus.OK);
    }

    /**
     * Deshabilitar un Usuario
     */
    public ResponseEntity<String> deshabilitarUsuario(String cedula) {
        logger.info("deshabilitarUsuario()");
        logger.info("Deshabilitando usuario con cedula: " + cedula);
        UsuarioEntity usuarioEncontrado = usuarioRepo.findByCedula(cedula).orElseThrow(
                () -> new ResourceNotFoundException("Usuario con cedula " + cedula + " no encontrado"));
        usuarioEncontrado.setEstado("I");
        usuarioRepo.save(usuarioEncontrado);
        logger.info("200 OK: Usuario deshabilitado correctamente");
        return new ResponseEntity<>("Usuario desactivado correctamente", HttpStatus.OK);
    }

    /**
     * Bloquear un Usuario
     */
    public ResponseEntity<String> bloquearUsuario(String cedula) {
        logger.info("bloquearUsuario()");
        logger.info("Bloqueando usuario con cedula: " + cedula);
        UsuarioEntity usuarioEncontrado = usuarioRepo.findByCedula(cedula)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con cedula " + cedula + " no encontrado"));
        usuarioEncontrado.setEstado("B");
        usuarioRepo.save(usuarioEncontrado);
        logger.info("200 OK: Usuario bloqueado correctamente");
        return new ResponseEntity<>("Usuario bloqueado correctamente", HttpStatus.OK);
    }

    /**
     * Actualizar un Usuario
     */
    @Transactional
    public ResponseEntity<UsuarioEntity> actualizarUsuario(Long id, UsuarioEntity usuario) {
        logger.info("actualizarUsuario()");
        logger.info("Actualizando usuario con id: " + id);

        UsuarioEntity usuarioEncontrado = usuarioRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        usuarioEncontrado.setNombres(usuario.getNombres());
        usuarioEncontrado.setApellidos(usuario.getApellidos());
        usuarioEncontrado.setEmail(usuario.getEmail());
        usuarioEncontrado.setCelular(usuario.getCelular());

        if (usuarioEncontrado.getUsuarioRoles() != null) {
            usuarioEncontrado.getUsuarioRoles().forEach(usuarioRol -> usuarioRol.setEstado("N"));
        }
        if (usuarioEncontrado.getUsuarioAreas() != null) {
            usuarioEncontrado.getUsuarioAreas().forEach(usuarioArea -> usuarioArea.setEstado("N"));
        }

        ZonedDateTime utc5 = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalDateTime localDateTimeUTC5 = utc5.toLocalDateTime();
        usuarioEncontrado.setFechaModificacion(localDateTimeUTC5);

        Set<UsuarioRolEntity> usuarioRolesNuevos = usuario.getUsuarioRoles().stream()
                .map(rol -> rolRepo.findByNombre(rol.getRol().getNombre()).orElse(null))
                .filter(Objects::nonNull)
                .filter(rol -> "A".equals(rol.getEstado()))
                .map(rol -> {
                    UsuarioRolEntity usuarioRol = new UsuarioRolEntity();
                    usuarioRol.setUsuario(usuarioEncontrado);
                    usuarioRol.setRol(rol);
                    usuarioRol.setFechaAsignacion(localDateTimeUTC5);
                    usuarioRol.setEstado("A");
                    return usuarioRol;
                })
                .collect(Collectors.toSet());

        Set<UsuarioAreaEntity> usuarioAreasNuevos = usuario.getUsuarioAreas().stream()
                .map(area -> areaRepo.findById(area.getArea().getIdArea()).orElse(null))
                .filter(Objects::nonNull)
                .filter(area -> "A".equals(area.getEstado()))
                .map(area -> {
                    UsuarioAreaEntity usuarioArea = new UsuarioAreaEntity();
                    usuarioArea.setUsuario(usuarioEncontrado);
                    usuarioArea.setArea(area);
                    usuarioArea.setFechaAsignacion(localDateTimeUTC5);
                    usuarioArea.setEstado("A");
                    return usuarioArea;
                })
                .collect(Collectors.toSet());

        if (!usuarioRolesNuevos.isEmpty()) {
            usuarioEncontrado.setUsuarioRoles(usuarioRolesNuevos);
        } else {
            logger.error("Se registró el usuario sin ningún rol");
        }

        if (!usuarioAreasNuevos.isEmpty()) {
            usuarioEncontrado.setUsuarioAreas(usuarioAreasNuevos);
        } else {
            logger.error("Se registró el usuario sin ninguna área asignada");
        }

        UsuarioEntity usuarioGuardado = usuarioRepo.save(usuarioEncontrado);
        logger.info("Usuario actualizado correctamente");
        return new ResponseEntity<>(usuarioGuardado, HttpStatus.OK);
    }
}
