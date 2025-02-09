package com.blautech.pruebaTecnica.demo.api.users.model;

public class PasswordResetRequest {

    private String email;

    // Constructor
    public PasswordResetRequest() {}

    public PasswordResetRequest(String email) {
        this.email = email;
    }

    // Getter y Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
