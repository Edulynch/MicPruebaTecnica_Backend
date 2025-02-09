package com.blautech.pruebaTecnica.demo.api.users.repository;

import com.blautech.pruebaTecnica.demo.api.users.model.PasswordResetRequestModel;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequestModel, Long> {

    // Buscar una solicitud de restablecimiento de contraseña por el usuario y el código de restablecimiento
    Optional<PasswordResetRequestModel> findByUserAndResetCode(User user, String resetCode);

    // Buscar una solicitud de restablecimiento por el código de restablecimiento
    Optional<PasswordResetRequestModel> findByResetCode(String resetCode);

    // Buscar una solicitud de restablecimiento por el usuario y que esté vigente (no ha expirado)
    Optional<PasswordResetRequestModel> findByUserAndExpirationTimeAfter(User user, LocalDateTime currentTime);
}
