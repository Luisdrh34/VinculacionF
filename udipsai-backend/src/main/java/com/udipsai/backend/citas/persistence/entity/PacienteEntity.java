package com.udipsai.backend.citas.persistence.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paciente")
public class PacienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombresApellidos", length = 255, nullable = false)
    private String nombresApellidos;

    @Column(name = "cedula", length = 12, nullable = false)
    private String cedula;

    @Column(name = "fechaApertura", nullable = false)
    private LocalDate fechaApertura;

    @Column(name = "fechaNacimiento", nullable = true)
    private LocalDate fechaNacimiento;

    @Column(name = "edad", length = 255, nullable = true)
    private String edad;

    @Column(name = "telefono", length = 20, nullable = true)
    private String telefono;

    @Column(name = "celular", length = 20, nullable = true)
    private String celular;

    @Column(name = "ciudad", length = 255, nullable = true)
    private String ciudad;

    @Column(name = "barrio", length = 255, nullable = true)
    private String barrio;

    @Column(name = "domicilio", length = 255, nullable = true)
    private String domicilio;

    @Column(name = "institucionEducativa", length = 255, nullable = true)
    private String institucionEducativa;

    @Column(name = "tipoInstitucion", length = 255, nullable = true)
    private String tipoInstitucion;

    @Column(name = "sector", length = 255, nullable = true)
    private String sector;

    @Column(name = "jornada", length = 255, nullable = true)
    private String jornada;

    @Column(name = "telefonoInstitucion", length = 20, nullable = true)
    private String telefonoInstitucion;

    @Column(name = "anioEducacion", length = 255, nullable = true)
    private String anioEducacion;

    @Column(name = "paralelo", length = 255, nullable = true)
    private String paralelo;

    @Column(name = "perteneceInclusion", length = 5, nullable = true)
    private String perteneceInclusion;

    @Column(name = "tieneDiscapacidad", length = 5, nullable = true)
    private String tieneDiscapacidad;

    @Column(name = "portadorCarnet", length = 5, nullable = true)
    private String portadorCarnet;

    @Column(name = "diagnostico", length = 255, nullable = true)
    private String diagnostico;

    @Column(name = "motivoConsulta", length = 255, nullable = true)
    private String motivoConsulta;

    @Column(name = "observaciones", length = 255, nullable = true)
    private String observaciones;

    @Column(name = "nombreExaminador", length = 255, nullable = true)
    private String nombreExaminador;

    @Column(name = "anotaciones", length = 255, nullable = true)
    private String anotaciones;
}
