package com.blautech.pruebaTecnica.demo.api.users.service;

import com.blautech.pruebaTecnica.demo.api.users.exception.PasswordResetException;
import com.blautech.pruebaTecnica.demo.api.users.model.PasswordResetRequestModel;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.blautech.pruebaTecnica.demo.api.users.repository.PasswordResetRequestRepository;
import com.blautech.pruebaTecnica.demo.api.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetRequestRepository passwordResetRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // Tiempo de validez del código en minutos
    private static final int RESET_CODE_VALIDITY_MINUTES = 15;
    // Tiempo mínimo entre solicitudes para evitar abusos (en minutos)
    private static final int MIN_INTERVAL_BETWEEN_REQUESTS_MINUTES = 5;

    /**
     * Solicita el restablecimiento de la contraseña.
     * Genera un código y lo envía al correo del usuario.
     * Controla que no se realicen solicitudes demasiado seguidas.
     *
     * @param email El correo del usuario.
     */
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new PasswordResetException("Usuario no encontrado"));

        LocalDateTime now = LocalDateTime.now();

        // Verificar si ya existe una solicitud activa para este usuario (con expirationTime mayor que ahora)
        Optional<PasswordResetRequestModel> existingRequestOpt =
                passwordResetRequestRepository.findByUserAndExpirationTimeAfter(user, now);
        if (existingRequestOpt.isPresent()) {
            PasswordResetRequestModel existingRequest = existingRequestOpt.get();
            // Si la última solicitud fue realizada hace menos de MIN_INTERVAL_BETWEEN_REQUESTS_MINUTES, rechazar la nueva solicitud
            if (existingRequest.getRequestTime().plusMinutes(MIN_INTERVAL_BETWEEN_REQUESTS_MINUTES).isAfter(now)) {
                throw new PasswordResetException("Ya se ha solicitado el restablecimiento recientemente. Por favor, inténtelo más tarde.");
            }
            // Actualizar la solicitud existente con un nuevo código y nueva expiración
            String newResetCode = generateResetCode();
            existingRequest.setResetCode(newResetCode);
            existingRequest.setRequestTime(now);
            existingRequest.setExpirationTime(now.plusMinutes(RESET_CODE_VALIDITY_MINUTES));
            passwordResetRequestRepository.save(existingRequest);
            sendResetEmail(user.getEmail(), newResetCode);
            return;
        }

        // Si no existe una solicitud activa, crear una nueva
        String resetCode = generateResetCode();
        PasswordResetRequestModel resetRequest = new PasswordResetRequestModel(
                user,
                resetCode,
                now,
                now.plusMinutes(RESET_CODE_VALIDITY_MINUTES)
        );
        passwordResetRequestRepository.save(resetRequest);
        sendResetEmail(user.getEmail(), resetCode);
    }

    /**
     * Valida el código de restablecimiento de contraseña.
     *
     * @param email     El correo del usuario.
     * @param resetCode El código de restablecimiento.
     * @return La solicitud de restablecimiento válida.
     */
    public PasswordResetRequestModel validateResetCode(String email, String resetCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new PasswordResetException("Usuario no encontrado"));

        PasswordResetRequestModel resetRequest = passwordResetRequestRepository
                .findByUserAndResetCode(user, resetCode)
                .orElseThrow(() -> new PasswordResetException("Código de restablecimiento inválido"));

        if (resetRequest.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new PasswordResetException("El código ha expirado");
        }
        return resetRequest;
    }

    /**
     * Actualiza la contraseña del usuario y elimina la solicitud de restablecimiento para evitar reusos.
     *
     * @param email       El correo del usuario.
     * @param resetCode   El código de restablecimiento.
     * @param newPassword La nueva contraseña del usuario.
     */
    public void updatePassword(String email, String resetCode, String newPassword) {
        PasswordResetRequestModel resetRequest = validateResetCode(email, resetCode);

        User user = resetRequest.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        passwordResetRequestRepository.delete(resetRequest);
    }

    /**
     * Genera un código aleatorio de restablecimiento utilizando SecureRandom.
     *
     * @return Un código de 6 dígitos.
     */
    private String generateResetCode() {
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(1000000); // Genera un número entre 0 y 999999
        return String.format("%06d", code);
    }

    /**
     * Envía un correo con el código de restablecimiento.
     *
     * @param toEmail   El correo del usuario.
     * @param resetCode El código de restablecimiento.
     */
    private void sendResetEmail(String toEmail, String resetCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Solicitud de restablecimiento de contraseña");
        message.setText("Su código de restablecimiento es: " + resetCode);
        mailSender.send(message);
    }
}
