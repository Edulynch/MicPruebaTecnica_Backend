package com.blautech.pruebaTecnica.demo.api.order.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pendiente"),
    CONFIRMED("Confirmado"),
    PROCESSING("Procesando"),
    SHIPPED("Enviado"),
    DELIVERED("Entregado"),
    CANCELLED("Cancelado");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
}
