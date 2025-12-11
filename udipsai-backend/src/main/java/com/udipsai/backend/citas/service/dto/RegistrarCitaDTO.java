package com.udipsai.backend.citas.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Clase que representa una el registro de una Cita en la capa de transferencia de datos.
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarCitaDTO {
    private Long fichaPaciente;
    private Long profesionalId;
    private Long areaId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime hora;
}
