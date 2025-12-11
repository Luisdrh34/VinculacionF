package com.udipsai.backend.usuarios.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
 * Clase que representa una Secretaria en la capa de transferencia de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecretariaDTO extends UsuarioDTO {
    private Long idSecretaria;
    private String secEstado;
}
