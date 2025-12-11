package com.udipsai.backend.citas.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
 * Clase que representa un Paciente en la capa de transferencia de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {
    private Long id;
    private String cedula;
    private LocalDate fechaApertura;
    private LocalDate fechaNacimiento;
    private String edad;
    private String nombresApellidos;
    private String telefono;
    private String celular;
    private String ciudad;
    private String barrio;
    private String domicilio;
    private String institucionEducativa;
    private String tipoInstitucion;
    private String sector;
    private String jornada;
    private String telefonoInstitucion;
    private String anioEducacion;
    private String paralelo;
    private String perteneceInclusion;
    private String tieneDiscapacidad;
    private String portadorCarnet;
    private String diagnostico;
    private String motivoConsulta;
    private String observaciones;
    private String nombreExaminador;
    private String anotaciones;
}
