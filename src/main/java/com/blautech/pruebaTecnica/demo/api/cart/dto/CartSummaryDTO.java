package com.blautech.pruebaTecnica.demo.api.cart.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartSummaryDTO {
    private List<CartItemDTO> items;
    private BigDecimal total;
}
