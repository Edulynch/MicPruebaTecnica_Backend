package com.blautech.pruebaTecnica.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDetails {
    private String title;
    private int status;
    private String detail;
    private String instance;
    private String errorCode; // Campo para personalizar el código de error
}
