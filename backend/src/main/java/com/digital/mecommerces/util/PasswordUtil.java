package com.digital.mecommerces.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode("password153");
        System.out.println("Contraseña cifrada: " + encryptedPassword);
    }
}
