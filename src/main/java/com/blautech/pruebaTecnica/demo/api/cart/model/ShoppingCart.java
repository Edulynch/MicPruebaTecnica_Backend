package com.blautech.pruebaTecnica.demo.api.cart.model;

import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    // Relación 1:1 con el usuario
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relación 1:N con los ítems del carrito
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // Se serializan los ítems
    private List<ShoppingCartItem> items = new ArrayList<>();
}
