package com.blautech.pruebaTecnica.demo.api.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
