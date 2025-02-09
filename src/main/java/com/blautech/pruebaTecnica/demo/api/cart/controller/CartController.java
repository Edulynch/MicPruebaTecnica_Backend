package com.blautech.pruebaTecnica.demo.api.cart.controller;

import com.blautech.pruebaTecnica.demo.api.cart.model.ShoppingCart;
import com.blautech.pruebaTecnica.demo.api.cart.dto.CartSummaryDTO;
import com.blautech.pruebaTecnica.demo.api.cart.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ShoppingCartService shoppingCartService;

    // Endpoint para consultar el carrito actual del usuario
    @GetMapping
    public ResponseEntity<ShoppingCart> getCart() {
        ShoppingCart cart = shoppingCartService.getCartForCurrentUser();
        return ResponseEntity.ok(cart);
    }

    // Endpoint para agregar un ítem al carrito
    @PostMapping("/items")
    public ResponseEntity<ShoppingCart> addItem(@RequestParam Long productId, @RequestParam Integer quantity) {
        ShoppingCart cart = shoppingCartService.addItemToCart(productId, quantity);
        return ResponseEntity.ok(cart);
    }

    // Endpoint para eliminar un ítem del carrito
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ShoppingCart> removeItem(@PathVariable Long itemId) {
        ShoppingCart cart = shoppingCartService.removeItemFromCart(itemId);
        return ResponseEntity.ok(cart);
    }

    // Nuevo endpoint para actualizar la cantidad de un ítem en el carrito
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartSummaryDTO> updateItemQuantity(@PathVariable Long itemId, @RequestParam Integer quantity) {
        shoppingCartService.updateItemQuantity(itemId, quantity);
        CartSummaryDTO summary = shoppingCartService.getCartSummary();
        return ResponseEntity.ok(summary);
    }

    // Nuevo endpoint para obtener el resumen del carrito mediante DTO
    @GetMapping("/summary")
    public ResponseEntity<CartSummaryDTO> getCartSummary() {
        CartSummaryDTO summary = shoppingCartService.getCartSummary();
        return ResponseEntity.ok(summary);
    }
}
