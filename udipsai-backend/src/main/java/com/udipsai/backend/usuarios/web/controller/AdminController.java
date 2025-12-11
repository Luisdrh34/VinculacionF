package com.udipsai.backend.usuarios.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.udipsai.backend.usuarios.service.AdminService;
import com.udipsai.backend.usuarios.service.dto.AdminDTO;
import com.udipsai.backend.usuarios.service.dto.RegistrarAdminDTO;

/*
 * Controlador para los Admins.
*/
@RestController
@RequestMapping("/api/admins")
public class AdminController {
    @Autowired
    private AdminService adminServ;

    // Obtener los Admins activos.
    @GetMapping
    public ResponseEntity<Page<AdminDTO>> obtenerAdmins(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return adminServ.obtenerAdmins(pageable);
    }

    // Obtener un Admin especifico.
    @GetMapping("/{cedula}")
    public ResponseEntity<AdminDTO> obtenerAdmin(@PathVariable String cedula) {
        return adminServ.obtenerAdmin(cedula);
    }

    // Obtener todos los Admins (A, I, B y N).
    @GetMapping("/todos")
    public ResponseEntity<Page<AdminDTO>> obtenerAdminsTodos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return adminServ.obtenerAdminsTodos(pageable);
    }

    // Obtener los Admins inactivos.
    @GetMapping("/inactivos")
    public ResponseEntity<Page<AdminDTO>> obtenerAdminsInactivos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return adminServ.obtenerAdminsEstado("I", pageable);
    }

    // Obtener los Admins bloqueados.
    @GetMapping("/bloqueados")
    public ResponseEntity<Page<AdminDTO>> obtenerAdminsBloqueados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return adminServ.obtenerAdminsEstado("B", pageable);
    }

    // Obtener los Admins eliminados.
    @GetMapping("/eliminados")
    public ResponseEntity<Page<AdminDTO>> obtenerAdminsEliminados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return adminServ.obtenerAdminsEstado("N", pageable);
    }

    // Registrar un Admin.
    @PostMapping
    public ResponseEntity<AdminDTO> registrarAdmin(@RequestBody RegistrarAdminDTO admin) {
        return adminServ.registrarAdmin(admin);
    }

    // Actualizar un Admin.
    @PutMapping("/{cedula}")
    public ResponseEntity<AdminDTO> actualizarAdmin(@PathVariable String cedula,
            @RequestBody AdminDTO admin) {
        return adminServ.actualizarAdmin(cedula, admin);
    }

    // Eliminar un Admin.
    @DeleteMapping("/{cedula}")
    public ResponseEntity<String> eliminarAdmin(@PathVariable String cedula) {
        return adminServ.eliminarAdmin(cedula);
    }

    // Activar un Admin.
    @PatchMapping("/activar/{cedula}")
    public ResponseEntity<String> activarAdmin(@PathVariable String cedula) {
        return adminServ.activarAdmin(cedula);
    }

    // Desactivar un Admin.
    @PatchMapping("/desactivar/{cedula}")
    public ResponseEntity<String> desactivarAdmin(@PathVariable String cedula) {
        return adminServ.desactivarAdmin(cedula);
    }

    // Bloquear un Admin.
    @PatchMapping("/bloquear/{cedula}")
    public ResponseEntity<String> bloquearAdmin(@PathVariable String cedula) {
        return adminServ.bloquearAdmin(cedula);
    }
}
