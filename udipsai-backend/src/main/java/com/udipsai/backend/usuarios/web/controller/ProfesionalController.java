package com.udipsai.backend.usuarios.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.udipsai.backend.usuarios.service.ProfesionalService;
import com.udipsai.backend.usuarios.service.dto.ProfesionalDTO;
import com.udipsai.backend.usuarios.service.dto.RegistrarProfesionalDTO;

/*
 * Controlador para los Profesionales.
*/
@RestController
@RequestMapping("/api/profesionales")
public class ProfesionalController {
    @Autowired
    private ProfesionalService profesionalServ;

    // Obtener los Profesionales activos.
    @GetMapping
    public ResponseEntity<Page<ProfesionalDTO>> obtenerProfesionales(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return profesionalServ.obtenerProfesionales(pageable);
    }

    // obtener profesionales activos por area
    @GetMapping("/area/{area}")
    public ResponseEntity<?> obtenerProfesionalesArea(@PathVariable Long area,
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return profesionalServ.obtenerProfesionalesArea(area, pageable);
    }

    // Obtener un Profesional especifico.
    @GetMapping("/{cedula}")
    public ResponseEntity<ProfesionalDTO> obtenerProfesional(@PathVariable String cedula) {
        return profesionalServ.obtenerProfesional(cedula);
    }

    // Obtener un Profesional especifico por su Id.
    @GetMapping("/id/{id}")
    public ResponseEntity<ProfesionalDTO> obtenerProfesionalPorId(@PathVariable Long id) {
        return profesionalServ.obtenerProfesionalPorId(id);
    }

    // Obtener profesional por usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerProfesionalPorUsuario(@PathVariable Long idUsuario) {
        return profesionalServ.obtenerProfesionalPorUsuario(idUsuario);
    }

    // Obtener todos los Profesionales (A, I, B y N).
    @GetMapping("/todos")
    public ResponseEntity<Page<ProfesionalDTO>> obtenerProfesionalesTodos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return profesionalServ.obtenerProfesionalesTodos(pageable);
    }

    // Obtener profesional por filtro
    @GetMapping("/filtro")
    public ResponseEntity<?> obtenerProfesionalesPorFiltro(@RequestParam(required = false) String filtro,
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return profesionalServ.obtenerProfesionalesFiltro(filtro, pageable);
    }

    // Obtener los Profesionales inactivos.
    @GetMapping("/inactivos")
    public ResponseEntity<Page<ProfesionalDTO>> obtenerProfesionalesInactivos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return profesionalServ.obtenerProfesionalesEstado("I", pageable);
    }

    // Obtener los Profesionales bloqueados.
    @GetMapping("/bloqueados")
    public ResponseEntity<Page<ProfesionalDTO>> obtenerProfesionalesBloqueados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return profesionalServ.obtenerProfesionalesEstado("B", pageable);
    }

    // Obtener los Profesionales eliminados.
    @GetMapping("/eliminados")
    public ResponseEntity<Page<ProfesionalDTO>> obtenerProfesionalesEliminados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return profesionalServ.obtenerProfesionalesEstado("N", pageable);
    }

    // Registrar un Profesional.
    @PostMapping
    public ResponseEntity<ProfesionalDTO> registrarProfesional(@RequestBody RegistrarProfesionalDTO profesional) {
        return profesionalServ.registrarProfesional(profesional);
    }

    // Actualizar un Profesional.
    @PutMapping("/{id}")
    public ResponseEntity<ProfesionalDTO> actualizarProfesional(@PathVariable Long id,
            @RequestBody ProfesionalDTO profesional) {
        return profesionalServ.actualizarProfesional(id, profesional);
    }

    // Eliminar un Profesional.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProfesional(@PathVariable Long id) {
        return profesionalServ.eliminarProfesional(id);
    }

    // Activar un Profesional.
    @PatchMapping("/activar/{cedula}")
    public ResponseEntity<String> activarProfesional(@PathVariable String cedula) {
        return profesionalServ.activarProfesional(cedula);
    }

    // Desactivar un Profesional.
    @PatchMapping("/desactivar/{cedula}")
    public ResponseEntity<String> desactivarProfesional(@PathVariable String cedula) {
        return profesionalServ.desactivarProfesional(cedula);
    }

    // Bloquear un Profesional.
    @PatchMapping("/bloquear/{cedula}")
    public ResponseEntity<String> bloquearProfesional(@PathVariable String cedula) {
        return profesionalServ.bloquearProfesional(cedula);
    }
}
