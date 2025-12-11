package com.udipsai.backend.auth.service.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String cedula;
    private String contrasenia;
    private String estado;
    private String email;
    private Role rol;

    public enum Role {
        USER, ADMIN
    }

}
