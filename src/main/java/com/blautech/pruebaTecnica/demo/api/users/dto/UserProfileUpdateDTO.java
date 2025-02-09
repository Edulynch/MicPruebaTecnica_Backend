package com.blautech.pruebaTecnica.demo.api.users.dto;

import com.blautech.pruebaTecnica.demo.validation.Adult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileUpdateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String shippingAddress;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Adult
    private LocalDate birthDate;

    // La contraseña es opcional en la actualización
    private String password;
}
