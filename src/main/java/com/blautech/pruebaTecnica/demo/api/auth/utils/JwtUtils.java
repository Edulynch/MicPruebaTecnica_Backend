package com.blautech.pruebaTecnica.demo.api.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private SecretKey jwtSecretKey;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs:3600000}")
    private long jwtExpirationMs;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("La clave secreta debe tener al menos 32 bytes");
        }
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token JWT basado en el subject y los roles.
     */
    public String generateToken(String subject, List<String> roles) {
        return Jwts.builder()
                .subject(subject)
                .claim("roles", String.join(",", roles))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey)
                .compact();
    }

    /**
     * Valida el token y retorna el subject (por ejemplo, el email del usuario).
     * Se robustece el proceso verificando que el subject no sea nulo o vacío,
     * y se captura cualquier excepción relacionada con el token.
     */
    public String validateTokenAndGetSubject(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(token);
            String subject = jws.getPayload().getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                throw new JwtException("El token no contiene un subject válido");
            }
            return subject;
        } catch (JwtException e) {
            throw new JwtException("Token inválido o expirado", e);
        }
    }

    /**
     * Extrae los roles (convertidos a GrantedAuthority) del token.
     * Se verifica y se captura cualquier error en el proceso.
     */
    public List<GrantedAuthority> getAuthorities(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(token);
            String rolesString = jws.getPayload().get("roles", String.class);
            if (rolesString != null && !rolesString.isEmpty()) {
                return Arrays.stream(rolesString.split(","))
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (JwtException e) {
            throw new JwtException("Token inválido o expirado", e);
        }
    }
}
