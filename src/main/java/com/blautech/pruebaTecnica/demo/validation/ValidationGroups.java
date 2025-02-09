package com.blautech.pruebaTecnica.demo.validation;

import org.springframework.validation.annotation.Validated;

@Validated
public class ValidationGroups {
    public interface CreateUser {}
    public interface UpdateUser {}
}
