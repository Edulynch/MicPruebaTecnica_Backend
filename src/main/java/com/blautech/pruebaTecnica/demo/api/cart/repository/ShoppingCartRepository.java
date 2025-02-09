package com.blautech.pruebaTecnica.demo.api.cart.repository;

import com.blautech.pruebaTecnica.demo.api.cart.model.ShoppingCart;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}
