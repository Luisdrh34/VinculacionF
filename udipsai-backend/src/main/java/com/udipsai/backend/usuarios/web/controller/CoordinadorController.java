package com.udipsai.backend.usuarios.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.udipsai.backend.usuarios.service.CoordinadorService;
import com.udipsai.backend.usuarios.service.dto.CoordinadorDTO;
import com.udipsai.backend.usuarios.service.dto.RegistrarCoordinadorDTO;

/*
 * Controlador para los Coordinadores.
*/
@RestController
@RequestMapping("/api/coordinadores")
public class CoordinadorController {
    @Autowired
    private CoordinadorService coordinadorServ;

    // Obtener los Coordinadores activos.
    @GetMapping
    public ResponseEntity<Page<CoordinadorDTO>> obtenerCoordinadores(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return coordinadorServ.obtenerCoordinadores(pageable);
    }

    // Obtener un Coordinador especifico.
    @GetMapping("/{cedula}")
    public ResponseEntity<CoordinadorDTO> obtenerCoordinador(@PathVariable String cedula) {
        return coordinadorServ.obtenerCoordinador(cedula);
    }

    // Obtener todos los Coordinadores (A, I, B y N).
    @GetMapping("/todos")
    public ResponseEntity<Page<CoordinadorDTO>> obtenerCoordinadoresTodos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return coordinadorServ.obtenerCoordinadoresTodos(pageable);
    }

    // Obtener coordinadores filtrados por nombre apellido o cedula
    @GetMapping("/filtro")
    public ResponseEntity<Page<CoordinadorDTO>> obtenerCoordinadoresPorFiltro(
            @RequestParam(required = false) String filtro,
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return coordinadorServ.findCoordinadoresFiltro(filtro, pageable);
    }

    // Obtener los Coordinadores inactivos.
    @GetMapping("/inactivos")
    public ResponseEntity<Page<CoordinadorDTO>> obtenerCoordinadoresInactivos(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return coordinadorServ.obtenerCoordinadoresEstado("I", pageable);
    }

    // Obtener los Coordinadores bloqueados.
    @GetMapping("/bloqueados")
    public ResponseEntity<Page<CoordinadorDTO>> obtenerCoordinadoresBloqueados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return coordinadorServ.obtenerCoordinadoresEstado("B", pageable);
    }

    // Obtener los Coordinadores eliminados.
    @GetMapping("/eliminados")
    public ResponseEntity<Page<CoordinadorDTO>> obtenerCoordinadoresEliminados(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return coordinadorServ.obtenerCoordinadoresEstado("N", pageable);
    }

    // Registrar un Coordinador.

    @PostMapping
    public ResponseEntity<CoordinadorDTO> registrarCoordinador(@RequestBody RegistrarCoordinadorDTO coordinador) {
        return coordinadorServ.registrarCoordinador(coordinador);
    }

    // Actualizar un Coordinador.
    @PutMapping("/{cedula}")
    public ResponseEntity<CoordinadorDTO> actualizarCoordinador(@PathVariable String cedula,
            @RequestBody CoordinadorDTO coordinador) {
        return coordinadorServ.actualizarCoordinador(cedula, coordinador);
    }

    // Eliminar un Coordinador.
    @DeleteMapping("/{cedula}")
    public ResponseEntity<String> eliminarCoordinador(@PathVariable String cedula) {
        return coordinadorServ.eliminarCoordinador(cedula);
    }

    // Activar un Coordinador.
    @PatchMapping("/activar/{cedula}")
    public ResponseEntity<String> activarCoordinador(@PathVariable String cedula) {
        return coordinadorServ.activarCoordinador(cedula);
    }

    // Desactivar un Coordinador.
    @PatchMapping("/desactivar/{cedula}")
    public ResponseEntity<String> desactivarCoordinador(@PathVariable String cedula) {
        return coordinadorServ.desactivarCoordinador(cedula);
    }

    // Bloquear un Coordinador.
    @PatchMapping("/bloquear/{cedula}")
    public ResponseEntity<String> bloquearCoordinador(@PathVariable String cedula) {
        return coordinadorServ.bloquearCoordinador(cedula);
    }
}
