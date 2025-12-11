package com.udipsai.backend.auth.service.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String cedula;
    private String contrasenia;
}
