package com.blautech.pruebaTecnica.demo.api.users.controller;

import com.blautech.pruebaTecnica.demo.api.users.model.PasswordResetRequest;
import com.blautech.pruebaTecnica.demo.api.users.model.PasswordResetValidationModel;
import com.blautech.pruebaTecnica.demo.api.users.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/password-reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Solicita el restablecimiento de la contraseña.
     * Envía un código de verificación al correo del usuario.
     * @param email El correo del usuario para enviar el código de restablecimiento.
     * @return Respuesta indicando que el código de verificación fue enviado.
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        try {
            // Solicitar el restablecimiento de contraseña
            passwordResetService.requestPasswordReset(request.getEmail());

            return ResponseEntity.ok("Código de restablecimiento enviado al correo");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Restablece la contraseña utilizando el código de verificación y la nueva contraseña.
     * @param request El cuerpo de la solicitud con el correo, el código de verificación y la nueva contraseña.
     * @return Respuesta indicando que la contraseña fue restablecida correctamente.
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetValidationModel request) {
        try {
            // Validar el código de restablecimiento
            passwordResetService.validateResetCode(request.getEmail(), request.getResetCode());

            // Actualizar la contraseña
            passwordResetService.updatePassword(request.getEmail(), request.getResetCode(), request.getNewPassword());

            return ResponseEntity.ok("Contraseña actualizada con éxito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
