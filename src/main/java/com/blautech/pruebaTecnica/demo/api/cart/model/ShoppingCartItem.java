package com.blautech.pruebaTecnica.demo.api.cart.model;

import com.blautech.pruebaTecnica.demo.api.catalog.model.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "shopping_cart_items")
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Producto agregado (se asume que la entidad Product ya existe)
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Cantidad solicitada y precio actual (puede provenir del producto)
    private Integer quantity;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;
}
