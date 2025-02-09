package com.blautech.pruebaTecnica.demo.api.auth.controller;

import com.blautech.pruebaTecnica.demo.api.auth.dto.LoginResponse;
import com.blautech.pruebaTecnica.demo.api.auth.dto.TokenRequest;
import com.blautech.pruebaTecnica.demo.api.auth.dto.TokenResponse;
import com.blautech.pruebaTecnica.demo.api.auth.service.AuthService;
import com.blautech.pruebaTecnica.demo.api.auth.dto.LoginRequest;  // Crear un DTO para la solicitud
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenResponse> validate(@RequestBody TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();  // Obtener el token del DTO
        TokenResponse tokenResponse = authService.validateToken(token); // Obtener el TokenResponse
        return ResponseEntity.ok(tokenResponse);  // Devolver la respuesta con el email y el mensaje
    }

}
