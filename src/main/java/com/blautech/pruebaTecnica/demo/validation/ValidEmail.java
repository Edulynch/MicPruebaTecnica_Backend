package com.blautech.pruebaTecnica.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidEmail {
    String message() default "El email no tiene un formato válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
