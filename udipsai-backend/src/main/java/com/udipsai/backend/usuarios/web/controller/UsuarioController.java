package com.udipsai.backend.usuarios.web.controller;

import com.udipsai.backend.usuarios.persistence.entity.UsuarioEntity;
import com.udipsai.backend.common.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.udipsai.backend.usuarios.service.dto.CambiarContraseniaDTO;
import com.udipsai.backend.usuarios.service.dto.UsuarioDTO;

/*
 * Controlador para los Usuarios.
*/
@RestController
@RequestMapping("/api")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioServ;

    // Hello World
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World");
    }

    // Obtener todos los Usuarios activos.
    @GetMapping("/usuarios")
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuarios(@PageableDefault(page = 0, size = 5) Pageable pageable) {
        return usuarioServ.obtenerUsuarios(pageable);
    }

    // Obtener todos los Usuarios (A, I, B, N).
    @GetMapping("/usuarios/todos")
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosTodos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return usuarioServ.obtenerUsuariosTodos(pageable);
    }

    //
    // Obtener un Usuario especifico.
    @GetMapping("/usuarios/{cedula}")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable String cedula) {
        return usuarioServ.obtenerUsuario(cedula);
    }

    // Obtener todos los Usuarios inactivos.
    @GetMapping("/usuarios/inactivos")
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosInactivos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return usuarioServ.obtenerUsuariosInactivos(pageable);
    }

    // Obtener todos los Usuarios bloqueados.
    @GetMapping("/usuarios/bloqueados")
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosBloqueados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return usuarioServ.obtenerUsuariosBloqueados(pageable);
    }

    // Obtener todos los Usuarios eliminados.
    @GetMapping("/usuarios/eliminados")
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosEliminados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return usuarioServ.obtenerUsuariosEliminados(pageable);
    }

    // Actualizar un Usuario.
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioEntity> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioEntity usuario) {
        return usuarioServ.actualizarUsuario(id, usuario);
    }

    // Cambiar contraseña de un Usuario.
    @PatchMapping("/usuarios/cambiarContrasenia/{cedula}")
    public ResponseEntity<?> cambiarContrasenia(@PathVariable String cedula,
            @RequestBody CambiarContraseniaDTO peticion) {
        return usuarioServ.cambiarContrasenia(cedula, peticion);
    }

    // Eliminar un Usuario.
    @DeleteMapping("/usuarios/{cedula}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String cedula) {
        return usuarioServ.eliminarUsuario(cedula);
    }

    // Habilitar un Usuario.
    @PatchMapping("/usuarios/habilitar/{cedula}")
    public ResponseEntity<String> habilitarUsuario(@PathVariable String cedula) {
        return usuarioServ.habilitarUsuario(cedula);
    }

    // Deshabilitar un Usuario.
    @PatchMapping("/usuarios/deshabilitar/{cedula}")
    public ResponseEntity<String> deshabilitarUsuario(@PathVariable String cedula) {
        return usuarioServ.deshabilitarUsuario(cedula);
    }

    // Bloquear un Usuario.
    @PatchMapping("/usuarios/bloquear/{cedula}")
    public ResponseEntity<String> bloquearUsuario(@PathVariable String cedula) {
        return usuarioServ.bloquearUsuario(cedula);
    }
}
