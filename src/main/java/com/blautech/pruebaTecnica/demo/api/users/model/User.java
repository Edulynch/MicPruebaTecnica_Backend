package com.blautech.pruebaTecnica.demo.api.users.model;

import com.blautech.pruebaTecnica.demo.api.roles.model.Role;
import com.blautech.pruebaTecnica.demo.util.StatusConstants;
import com.blautech.pruebaTecnica.demo.validation.Adult;
import com.blautech.pruebaTecnica.demo.validation.ValidEmail;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @ValidEmail
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Adult(message = "El usuario debe ser mayor de 18 años")
    @Column(nullable = false)
    private LocalDate birthDate;

    // Status: 1 = activado, 0 = desactivado
    @Column(nullable = false)
    private Integer status;

    // Fecha de creación y última modificación
    @Column(nullable = false)
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @NotBlank(message = "La dirección de envío es obligatoria")
    @Column(nullable = false)
    private String shippingAddress;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = StatusConstants.ACTIVE;
        }
        createdDate = LocalDateTime.now();
        modifiedDate = null;
    }

    @PreUpdate
    public void preUpdate() {
        modifiedDate = LocalDateTime.now();
    }
}
