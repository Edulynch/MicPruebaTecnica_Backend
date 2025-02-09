package com.blautech.pruebaTecnica.demo.api.catalog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // URL o path de la imagen del artículo
    private String image;

    // Descripción del producto
    private String description;

    // Monto (precio) del producto
    private BigDecimal amount;

    // Cantidad disponible
    private Integer availableQuantity;

    // Status: 0 = desactivado, 1 = activado (se elige en la creación)
    private Integer status;

    // Fecha de creación (se asigna en @PrePersist)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // Fecha de modificación (nula en creación, se actualiza en @PreUpdate)
    private LocalDateTime modifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = null;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
}
