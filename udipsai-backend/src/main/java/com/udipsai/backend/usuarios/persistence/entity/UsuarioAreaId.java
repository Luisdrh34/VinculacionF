package com.udipsai.backend.usuarios.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAreaId implements Serializable {
    private Long usuario;
    private Long area;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UsuarioAreaId that = (UsuarioAreaId) o;
        return Objects.equals(usuario, that.usuario) && Objects.equals(area, that.area);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, area);
    }
}