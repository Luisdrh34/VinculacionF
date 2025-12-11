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

import com.udipsai.backend.usuarios.service.SecretariaService;
import com.udipsai.backend.usuarios.service.dto.SecretariaDTO;
import com.udipsai.backend.usuarios.service.dto.RegistrarSecretariaDTO;

/*
 * Controlador para las Secretarias.
*/
@RestController
@RequestMapping("/api/secretarias")
public class SecretariaController {
    @Autowired
    private SecretariaService secretariaServ;

    // Obtener las Secretarias activas.
    @GetMapping
    public ResponseEntity<Page<SecretariaDTO>> obtenerSecretarias(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return secretariaServ.obtenerSecretarias(pageable);
    }

    // Obtener una Secretaria especifica.
    @GetMapping("/{cedula}")
    public ResponseEntity<SecretariaDTO> obtenerSecretaria(@PathVariable String cedula) {
        return secretariaServ.obtenerSecretaria(cedula);
    }

    // Obtener todas las Secretarias (A, I, B y N).
    @GetMapping("/todas")
    public ResponseEntity<Page<SecretariaDTO>> obtenerSecretariasTodas(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return secretariaServ.obtenerSecretariasTodas(pageable);
    }

    // Obtener Secretaria por filtro
    @GetMapping("/filtro")
    public ResponseEntity<?> obtenerSecretariasPorFiltro(@PathVariable String filtro,
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return secretariaServ.obtenerSecretariasFiltro(filtro, pageable);
    }

    // Obtener las Secretarias inactivas.
    @GetMapping("/inactivas")
    public ResponseEntity<Page<SecretariaDTO>> obtenerSecretariasInactivas(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return secretariaServ.obtenerSecretariasEstado("I", pageable);
    }

    // Obtener las Secretarias bloqueadas.
    @GetMapping("/bloqueadas")
    public ResponseEntity<Page<SecretariaDTO>> obtenerSecretariasBloqueadas(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return secretariaServ.obtenerSecretariasEstado("B", pageable);
    }

    // Obtener las Secretarias eliminadas.
    @GetMapping("/eliminadas")
    public ResponseEntity<Page<SecretariaDTO>> obtenerSecretariasEliminadas(
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return secretariaServ.obtenerSecretariasEstado("N", pageable);
    }

    // Registrar una Secretaria.
    @PostMapping
    public ResponseEntity<SecretariaDTO> registrarSecretaria(@RequestBody RegistrarSecretariaDTO secretaria) {
        return secretariaServ.registrarSecretaria(secretaria);
    }

    // Actualizar una Secretaria.
    @PutMapping("/{cedula}")
    public ResponseEntity<SecretariaDTO> actualizarSecretaria(@PathVariable String cedula,
            @RequestBody SecretariaDTO secretaria) {
        return secretariaServ.actualizarSecretaria(cedula, secretaria);
    }

    // Eliminar una Secretaria.
    @DeleteMapping("/{cedula}")
    public ResponseEntity<String> eliminarSecretaria(@PathVariable String cedula) {
        return secretariaServ.eliminarSecretaria(cedula);
    }

    // Activar una Secretaria.
    @PatchMapping("/activar/{cedula}")
    public ResponseEntity<String> activarSecretaria(@PathVariable String cedula) {
        return secretariaServ.activarSecretaria(cedula);
    }

    // Desactivar una Secretaria.
    @PatchMapping("/desactivar/{cedula}")
    public ResponseEntity<String> desactivarSecretaria(@PathVariable String cedula) {
        return secretariaServ.desactivarSecretaria(cedula);
    }

    // Bloquear una Secretaria.
    @PatchMapping("/bloquear/{cedula}")
    public ResponseEntity<String> bloquearSecretaria(@PathVariable String cedula) {
        return secretariaServ.bloquearSecretaria(cedula);
    }
}
