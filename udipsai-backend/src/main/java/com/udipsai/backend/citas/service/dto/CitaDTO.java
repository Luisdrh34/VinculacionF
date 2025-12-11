package com.udipsai.backend.citas.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.udipsai.backend.usuarios.service.dto.AreaDTO;
import com.udipsai.backend.usuarios.service.dto.ProfesionalDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Clase que representa una Cita en la capa de transferencia de datos.
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO {
    private Long citaId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;
    private String estado;
    private PacienteDTO paciente;
    private ProfesionalDTO profesional;
    private AreaDTO area;
}
