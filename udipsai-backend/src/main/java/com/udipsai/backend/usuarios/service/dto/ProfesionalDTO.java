package com.udipsai.backend.usuarios.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
 * Clase que representa un Profesional en la capa de transferencia de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionalDTO extends UsuarioDTO {
    private Long idProfesional;
    private String profEstado;
    private String especialidad;
}
