package com.udipsai.backend.usuarios.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
 * Clase que representa un cambio de contraseña en la capa de transferencia de datos
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambiarContraseniaDTO {
    private String contrasenia;
    private String nuevaContrasenia;
}
