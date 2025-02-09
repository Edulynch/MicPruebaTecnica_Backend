package com.blautech.pruebaTecnica.demo.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String email;
    private String message;
}
