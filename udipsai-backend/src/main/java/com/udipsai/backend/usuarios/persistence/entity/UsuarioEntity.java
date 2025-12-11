package com.udipsai.backend.usuarios.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

import java.util.Set;
import java.time.LocalDateTime;

/*
 * Clase que representa un Usuario genérico en la capa de persistencia.
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false, unique = true)
    private Long idUsuario;

    @Column(name = "cedula", length = 12, nullable = false, unique = true)
    private String cedula;

    @Column(name = "contrasenia", length = 200, nullable = false)
    private String contrasenia;

    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "celular", nullable = true)
    private String celular;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    // A activo
    // I inactivo
    // N borrado
    // B bloqueado
    @Column(name = "estado", length = 5, nullable = false)
    private String estado;

    // ROLES
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UsuarioRolEntity> usuarioRoles;

    // AREAS
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UsuarioAreaEntity> usuarioAreas;
}
