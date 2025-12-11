package com.udipsai.backend.usuarios.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

/*
 * Clase que representa un Usuario en la capa de transferencia de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String cedula;
    private String estado;
    private String nombres;
    private String apellidos;
    private String email;
    private String celular;
    private Set<RolDTO> roles;
    private Set<AreaDTO> areas;
}
