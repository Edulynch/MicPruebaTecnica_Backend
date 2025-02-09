package com.blautech.pruebaTecnica.demo.api.auth.dto;

import com.blautech.pruebaTecnica.demo.api.users.model.User;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserWithoutPasswordDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String shippingAddress;
    private LocalDate birthDate;

    // Constructor a partir del objeto User
    public UserWithoutPasswordDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.shippingAddress = user.getShippingAddress();
        this.birthDate = user.getBirthDate();
    }
}
