package com.udipsai.backend.usuarios.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
 * Clase que representa un Rol en la capa de transferencia de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {
    private Long idRol;
    private String nombre;
    private String estado;
}