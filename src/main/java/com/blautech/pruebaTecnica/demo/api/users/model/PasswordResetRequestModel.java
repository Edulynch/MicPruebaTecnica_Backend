package com.blautech.pruebaTecnica.demo.api.users.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "password_reset_requests")
public class PasswordResetRequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String resetCode;

    private LocalDateTime expirationTime;

    private LocalDateTime requestTime;

    // Constructor personalizado sin incluir el id
    public PasswordResetRequestModel(User user, String resetCode, LocalDateTime requestTime, LocalDateTime expirationTime) {
        this.user = user;
        this.resetCode = resetCode;
        this.requestTime = requestTime;
        this.expirationTime = expirationTime;
    }
}
