package com.blautech.pruebaTecnica.demo.api.users.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String shippingAddress;
    private LocalDate birthDate;
}
