package com.blautech.pruebaTecnica.demo.api.cart.model;

import com.blautech.pruebaTecnica.demo.api.users.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación 1:1 con usuario (cada usuario tiene un carrito)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Lista de ítems del carrito; se usan cascadas para persistirlos junto con el carrito
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingCartItem> items = new ArrayList<>();
}
