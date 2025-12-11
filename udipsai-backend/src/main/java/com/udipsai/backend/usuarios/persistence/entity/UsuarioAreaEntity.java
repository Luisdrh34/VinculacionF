package com.udipsai.backend.usuarios.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
 * Clase que representa una relación entre Usuarios y Áreas en la capa de persistencia.
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(UsuarioAreaId.class)
@Table(name = "usuarios_areas")
public class UsuarioAreaEntity {
    @Id
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_area", nullable = false)
    private AreaEntity area;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    // A activo
    // I deshabilitado
    // N borrado
    // B bloqueado
    @Column(name = "estado", length = 5, nullable = false)
    private String estado;
}
