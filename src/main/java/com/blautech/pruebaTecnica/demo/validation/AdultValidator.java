package com.blautech.pruebaTecnica.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return false; // Se espera que @NotNull ya lo gestione, pero se refuerza aquí.
        }
        LocalDate adultDate = LocalDate.now().minusYears(18);
        return !birthDate.isAfter(adultDate); // Es válido si la fecha es igual o anterior a la fecha límite.
    }
}
