package com.blautech.pruebaTecnica.demo.api.cart.service;

import com.blautech.pruebaTecnica.demo.api.cart.model.ShoppingCart;
import com.blautech.pruebaTecnica.demo.api.cart.model.ShoppingCartItem;
import com.blautech.pruebaTecnica.demo.api.cart.dto.CartItemDTO;
import com.blautech.pruebaTecnica.demo.api.cart.dto.CartSummaryDTO;
import com.blautech.pruebaTecnica.demo.api.cart.repository.ShoppingCartRepository;
import com.blautech.pruebaTecnica.demo.api.catalog.model.Product;
import com.blautech.pruebaTecnica.demo.api.catalog.repository.ProductRepository;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.blautech.pruebaTecnica.demo.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // Obtiene el usuario autenticado a partir del token
    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));
    }

    @Transactional
    public ShoppingCart getCartForCurrentUser() {
        User user = getCurrentUser();
        return shoppingCartRepository.findByUser(user)
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart();
                    cart.setUser(user);
                    return shoppingCartRepository.save(cart);
                });
    }

    @Transactional
    public ShoppingCart addItemToCart(Long productId, Integer quantity) {
        ShoppingCart cart = getCartForCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        ShoppingCartItem item = new ShoppingCartItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getAmount());
        item.setCart(cart);
        cart.getItems().add(item);

        return shoppingCartRepository.save(cart);
    }

    @Transactional
    public ShoppingCart removeItemFromCart(Long itemId) {
        ShoppingCart cart = getCartForCurrentUser();
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        return shoppingCartRepository.save(cart);
    }

    @Transactional
    public ShoppingCart updateItemQuantity(Long itemId, Integer newQuantity) {
        ShoppingCart cart = getCartForCurrentUser();
        boolean found = false;
        Iterator<ShoppingCartItem> iterator = cart.getItems().iterator();
        while (iterator.hasNext()) {
            ShoppingCartItem item = iterator.next();
            if (item.getId().equals(itemId)) {
                if (newQuantity <= 0) {
                    iterator.remove();
                } else {
                    item.setQuantity(newQuantity);
                }
                found = true;
                break;
            }
        }
        if (!found) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado en el carrito");
        }
        return shoppingCartRepository.save(cart);
    }

    @Transactional
    public ShoppingCart clearCart(ShoppingCart cart) {
        cart.getItems().clear();
        return shoppingCartRepository.save(cart);
    }

    // Genera el resumen del carrito en un DTO
    public CartSummaryDTO getCartSummary() {
        ShoppingCart cart = getCartForCurrentUser();
        List<CartItemDTO> itemDTOs = cart.getItems().stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setId(item.getId());
            dto.setProductId(item.getProduct().getId());
            dto.setProductDescription(item.getProduct().getDescription());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return dto;
        }).collect(Collectors.toList());
        CartSummaryDTO summary = new CartSummaryDTO();
        summary.setItems(itemDTOs);
        BigDecimal total = itemDTOs.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotal(total);
        return summary;
    }
}
