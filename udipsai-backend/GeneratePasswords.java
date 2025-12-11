package com.udipsai.backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswords {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Password for '123456': " + encoder.encode("123456"));
        System.out.println("Password for 'admin': " + encoder.encode("admin"));
        System.out.println("Password for 'secretaria': " + encoder.encode("secretaria"));
    }
}
