package com.blautech.pruebaTecnica.demo.api.cart.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productDescription;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal; // Calculado como: price * quantity
}
