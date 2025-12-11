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

import com.udipsai.backend.usuarios.service.AreaService;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;

/*
 * Controlador para las Areas.
*/
@RestController
@RequestMapping("/api/areas")
public class AreaController {
    @Autowired
    private AreaService areaServ;

    // Obtener las Areas activas.
    @GetMapping
    public ResponseEntity<List<AreaDTO>> obtenerAreas() {
        return areaServ.obtenerAreas();
    }

    // Obtener un Area especifica por Id
    @GetMapping("/id/{id}")
    public ResponseEntity<AreaDTO> obtenerAreaPorId(@PathVariable Long id) {
        return areaServ.obtenerAreaPorId(id);
    }

    // Obtener un Area especifica por Nombre.
    @GetMapping("/{nombre}")
    public ResponseEntity<AreaDTO> obtenerAreaPorNombre(@PathVariable String nombre) {
        return areaServ.obtenerAreaPorNombre(nombre);
    }

    // Obtener todos las Areas (A, I, B y N).
    @GetMapping("/todas")
    public ResponseEntity<List<AreaDTO>> obtenerAreasTodas() {
        return areaServ.obtenerAreasTodas();
    }

    // Obtener las Areas inactivas.
    @GetMapping("/inactivas")
    public ResponseEntity<List<AreaDTO>> obtenerAreasInactivas() {
        return areaServ.obtenerAreasEstado("I");
    }

    // Obtener las Areas bloqueadas.
    @GetMapping("/bloqueadas")
    public ResponseEntity<List<AreaDTO>> obtenerAreasBloqueadas() {
        return areaServ.obtenerAreasEstado("B");
    }

    // Obtener las Areas eliminadas.
    @GetMapping("/eliminadas")
    public ResponseEntity<List<AreaDTO>> obtenerAreasEliminadas() {
        return areaServ.obtenerAreasEstado("N");
    }

    // Registrar Area.
    @PostMapping
    public ResponseEntity<AreaDTO> registrarArea(@RequestBody String nombre) {
        return areaServ.registrarArea(nombre);
    }

    // Actualizar Area.
    @PutMapping("/{id}")
    public ResponseEntity<AreaDTO> actualizarArea(@PathVariable Long id,
            @RequestBody String nombre) {
        return areaServ.actualizarArea(id, nombre);
    }

    // Eliminar Area.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarArea(@PathVariable Long id) {
        return areaServ.eliminarArea(id);
    }

    // Activar Area.
    @PatchMapping("/activar/{id}")
    public ResponseEntity<String> activarArea(@PathVariable Long id) {
        return areaServ.activarArea(id);
    }

    // Desactivar Area.
    @PatchMapping("/desactivar/{id}")
    public ResponseEntity<String> desactivarArea(@PathVariable Long id) {
        return areaServ.desactivarArea(id);
    }

    // Bloquear Area.
    @PatchMapping("/bloquear/{id}")
    public ResponseEntity<String> bloquearArea(@PathVariable Long id) {
        return areaServ.bloquearArea(id);
    }
}
