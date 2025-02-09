package com.blautech.pruebaTecnica.demo.api.order.service;

import com.blautech.pruebaTecnica.demo.api.order.model.Order;
import com.blautech.pruebaTecnica.demo.api.order.model.OrderItem;
import com.blautech.pruebaTecnica.demo.api.order.repository.OrderRepository;
import com.blautech.pruebaTecnica.demo.api.cart.model.ShoppingCart;
import com.blautech.pruebaTecnica.demo.api.cart.service.ShoppingCartService;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.blautech.pruebaTecnica.demo.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final UserRepository userRepository;

    // Obtiene el usuario autenticado
    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));
    }

    // Verifica que la orden exista y pertenezca al usuario (o que el usuario tenga rol WORKER/ADMIN)
    public Order getOrderForCurrentUser(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
        User currentUser = getCurrentUser();
        boolean isWorkerOrAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("WORKER") ||
                        role.getName().equalsIgnoreCase("ADMIN"));
        if (!isWorkerOrAdmin && !order.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
        }
        return order;
    }

    // Inicia un pedido a partir del carrito, creando un pedido en estado PENDING
    @Transactional
    public Order initiateOrder(String shippingAddress) {
        User user = getCurrentUser();
        ShoppingCart cart = shoppingCartService.getCartForCurrentUser();
        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
        }
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus("PENDING");
        order.setCreatedDate(LocalDateTime.now());
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(cartItem.getProduct());
            oi.setQuantity(cartItem.getQuantity());
            oi.setPrice(cartItem.getPrice());
            return oi;
        }).collect(Collectors.toList());
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        // No se limpia el carrito hasta confirmar el pedido
        return savedOrder;
    }

    // Permite actualizar la dirección de envío de un pedido PENDING
    @Transactional
    public Order updateShippingAddress(Long orderId, String newShippingAddress) {
        Order order = getOrderForCurrentUser(orderId);
        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede actualizar la dirección de un pedido ya confirmado");
        }
        order.setShippingAddress(newShippingAddress);
        return orderRepository.save(order);
    }

    // Confirma un pedido PENDING, genera el número de orden, cambia el estado a CONFIRMED y limpia el carrito
    @Transactional
    public Order confirmOrder(Long orderId) {
        Order order = getOrderForCurrentUser(orderId);
        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido ya fue confirmado");
        }
        order.setStatus("CONFIRMED");
        order.setOrderNumber(UUID.randomUUID().toString());
        Order savedOrder = orderRepository.save(order);
        ShoppingCart cart = shoppingCartService.getCartForCurrentUser();
        shoppingCartService.clearCart(cart);
        return savedOrder;
    }

    // Retorna las órdenes del usuario (o todas si es WORKER/ADMIN)
    public List<Order> getOrdersForCurrentUser() {
        User user = getCurrentUser();
        boolean isWorkerOrAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("WORKER") ||
                        role.getName().equalsIgnoreCase("ADMIN"));
        if (isWorkerOrAdmin) {
            return orderRepository.findAll();
        } else {
            return orderRepository.findByUser(user);
        }
    }

    // Retorna el detalle de una orden
    public Order getOrderDetail(Long orderId) {
        return getOrderForCurrentUser(orderId);
    }
}
