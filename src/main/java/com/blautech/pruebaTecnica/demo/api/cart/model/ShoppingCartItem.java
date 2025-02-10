package com.blautech.pruebaTecnica.demo.api.cart.model;

import com.blautech.pruebaTecnica.demo.api.catalog.model.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    // Relación con el producto
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Cantidad solicitada y precio en el momento de la compra
    private Integer quantity;
    private BigDecimal price;

    // Relación inversa al carrito
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference  // No se serializa la referencia al carrito para romper la recursión
    private ShoppingCart cart;
}
