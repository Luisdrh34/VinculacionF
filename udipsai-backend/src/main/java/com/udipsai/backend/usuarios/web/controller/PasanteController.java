package com.udipsai.backend.usuarios.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.udipsai.backend.usuarios.service.PasanteService;
import com.udipsai.backend.usuarios.service.dto.PasanteDTO;
import com.udipsai.backend.usuarios.service.dto.RegistrarPasanteDTO;

/*
 * Controlador para los Pasantes.
*/
@RestController
@RequestMapping("/api/pasantes")
@CrossOrigin(origins = "http://localhost:4200")
public class PasanteController {
    @Autowired
    private PasanteService pasanteServ;

    // Obtener los Pasantes activos.
    @GetMapping
    public ResponseEntity<Page<PasanteDTO>> obtenerPasantes(@PageableDefault(page = 0, size = 5) Pageable pageable) {
        return pasanteServ.obtenerPasantes(pageable);
    }

    // Obtener un Pasante especifico.
    @GetMapping("/{cedula}")
    public ResponseEntity<PasanteDTO> obtenerPasante(@PathVariable String cedula) {
        return pasanteServ.obtenerPasante(cedula);
    }

    // Obtener todos los Pasantes (A, I, B y N).
    @GetMapping("/todos")
    public ResponseEntity<Page<PasanteDTO>> obtenerPasantesTodos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return pasanteServ.obtenerPasantesTodos(pageable);
    }

    // Obtener un Pasante por filtro.
    @GetMapping("/filtro")
    public ResponseEntity<Page<PasanteDTO>> obtenerPasantesPorFiltro(
            @RequestParam(name = "filtro", required = true) String filtro,
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return pasanteServ.findPasantesFiltro(filtro, pageable);
    }

    // Obtener los Pasantes inactivos.
    @GetMapping("/inactivos")
    public ResponseEntity<Page<PasanteDTO>> obtenerPasantesInactivos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return pasanteServ.obtenerPasantesEstado("I", pageable);
    }

    // Obtener los Pasantes bloqueados.
    @GetMapping("/bloqueados")
    public ResponseEntity<Page<PasanteDTO>> obtenerPasantesBloqueados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return pasanteServ.obtenerPasantesEstado("B", pageable);
    }

    // Obtener los Pasantes eliminados.
    @GetMapping("/eliminados")
    public ResponseEntity<Page<PasanteDTO>> obtenerPasantesEliminados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return pasanteServ.obtenerPasantesEstado("N", pageable);
    }

    // Registrar un Pasante.
    @PostMapping
    public ResponseEntity<PasanteDTO> registrarPasante(@RequestBody RegistrarPasanteDTO pasante) {
        return pasanteServ.registrarPasante(pasante);
    }

    // Actualizar un Pasante.
    @PutMapping("/{cedula}")
    public ResponseEntity<PasanteDTO> actualizarPasante(@PathVariable String cedula,
            @RequestBody PasanteDTO pasante) {
        return pasanteServ.actualizarPasante(cedula, pasante);
    }

    // Eliminar un Pasante.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPasante(@PathVariable Long id) {
        return pasanteServ.eliminarPasante(id);
    }

    // Activar un Pasante.
    @PatchMapping("/activar/{cedula}")
    public ResponseEntity<String> activarPasante(@PathVariable String cedula) {
        return pasanteServ.activarPasante(cedula);
    }

    // Desactivar un Pasante.
    @PatchMapping("/desactivar/{cedula}")
    public ResponseEntity<String> desactivarPasante(@PathVariable String cedula) {
        return pasanteServ.desactivarPasante(cedula);
    }

    // Bloquear un Pasante.
    @PatchMapping("/bloquear/{id}")
    public ResponseEntity<String> bloquearPasante(@PathVariable Long id) {
        return pasanteServ.bloquearPasante(id);
    }
}
