package com.udipsai.backend.usuarios.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udipsai.backend.usuarios.service.RolService;
import com.udipsai.backend.usuarios.service.dto.RolDTO;

/*
 * Controlador para los Roles.
*/
@RestController
@RequestMapping("/api/roles")
public class RolController {
    @Autowired
    private RolService rolServ;

    // Obtener los Roles activos.
    @GetMapping
    public ResponseEntity<List<RolDTO>> obtenerRoles() {
        return rolServ.obtenerRoles();
    }

    // Obtener un Rol por Id
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerRolPorId(@PathVariable Long id) {
        return rolServ.obtenerRolPorId(id);
    }

    // Obtener un Rol por Nombre.
    @GetMapping("/{nombre}")
    public ResponseEntity<RolDTO> obtenerRolPorNombre(@PathVariable String nombre) {
        return rolServ.obtenerRolPorNombre(nombre);
    }

    // Obtener todos los Roles (A, I, B y N).
    @GetMapping("/todos")
    public ResponseEntity<List<RolDTO>> obtenerRolesTodos() {
        return rolServ.obtenerRolesTodos();
    }

    // Obtener los Roles inactivos.
    @GetMapping("/inactivos")
    public ResponseEntity<List<RolDTO>> obtenerRolesInactivos() {
        return rolServ.obtenerRolesEstado("I");
    }

    // Obtener los Roles bloqueados.
    @GetMapping("/bloqueados")
    public ResponseEntity<List<RolDTO>> obtenerRolesBloqueados() {
        return rolServ.obtenerRolesEstado("B");
    }

    // Obtener los Roles eliminados.
    @GetMapping("/eliminados")
    public ResponseEntity<List<RolDTO>> obtenerRolesEliminados() {
        return rolServ.obtenerRolesEstado("N");
    }

    // Registrar Rol.
    @PostMapping
    public ResponseEntity<RolDTO> registrarRol(@RequestBody String nombre) {
        return rolServ.registrarRol(nombre);
    }

    // Actualizar Rol.
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizarRol(@PathVariable Long id,
            @RequestBody String nombre) {
        return rolServ.actualizarRol(id, nombre);
    }

    // Eliminar Rol.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarRol(@PathVariable Long id) {
        return rolServ.eliminarRol(id);
    }

    // Activar Rol.
    @PatchMapping("/activar/{id}")
    public ResponseEntity<String> activarRol(@PathVariable Long id) {
        return rolServ.activarRol(id);
    }

    // Desactivar Rol.
    @PatchMapping("/desactivar/{id}")
    public ResponseEntity<String> desactivarRol(@PathVariable Long id) {
        return rolServ.desactivarRol(id);
    }

    // Bloquear Rol.
    @PatchMapping("/bloquear/{id}")
    public ResponseEntity<String> bloquearRol(@PathVariable Long id) {
        return rolServ.bloquearRol(id);
    }
}
