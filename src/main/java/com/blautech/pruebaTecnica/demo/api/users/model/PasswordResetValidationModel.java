package com.blautech.pruebaTecnica.demo.api.users.model;

public class PasswordResetValidationModel {

    private String email;
    private String resetCode;
    private String newPassword;

    // Constructor
    public PasswordResetValidationModel() {}

    public PasswordResetValidationModel(String email, String resetCode, String newPassword) {
        this.email = email;
        this.resetCode = resetCode;
        this.newPassword = newPassword;
    }

    // Getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
