package com.blautech.pruebaTecnica.demo.api.auth.service;

import com.blautech.pruebaTecnica.demo.api.auth.dto.LoginResponse;
import com.blautech.pruebaTecnica.demo.api.auth.dto.TokenResponse;
import com.blautech.pruebaTecnica.demo.api.auth.dto.UserWithoutPasswordDTO;
import com.blautech.pruebaTecnica.demo.api.roles.model.Role;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.blautech.pruebaTecnica.demo.api.users.repository.UserRepository;
import com.blautech.pruebaTecnica.demo.api.auth.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponse login(String email, String rawPassword) {
        // Buscar y validar al usuario (lo que ya tienes)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        if (!user.getStatus().equals(1)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario inactivo");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        // Extraer los nombres de los roles
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        // Generar token incluyendo los roles
        String token = jwtUtils.generateToken(user.getEmail(), roles);

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(user);
        return new LoginResponse(token, userWithoutPassword);
    }

    public TokenResponse validateToken(String token) {
        // Aquí validamos el token y obtenemos el email (sujeto)
        String email = jwtUtils.validateTokenAndGetSubject(token);

        // Devolvemos una respuesta con el email y un mensaje
        return new TokenResponse(email, "Token válido");
    }

}
