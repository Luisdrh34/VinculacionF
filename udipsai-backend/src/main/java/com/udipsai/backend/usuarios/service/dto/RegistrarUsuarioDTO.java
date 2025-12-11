package com.udipsai.backend.usuarios.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import com.udipsai.backend.usuarios.persistence.entity.AreaEntity;
import com.udipsai.backend.usuarios.persistence.entity.RolEntity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
 * Clase que representa el registro de un Usuario en la capa de transferencia de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarUsuarioDTO {
    private String cedula;
    private String contrasenia;
    private String nombres;
    private String apellidos;
    private String email;
    private String celular;
    private Set<RolEntity> roles;
    private Set<AreaEntity> areas;
}