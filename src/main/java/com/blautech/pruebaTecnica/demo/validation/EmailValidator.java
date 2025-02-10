package com.blautech.pruebaTecnica.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    // Ejemplo de regex más estricto que no permite guiones bajos en la parte del dominio
    private static final String EMAIL_REGEX =
            "^[\\w!#$%&'*+/=?`{|}~^.-]+@(?:(?!_)[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$";
    private Pattern pattern = Pattern.compile(EMAIL_REGEX);

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(email).matches();
    }
}
