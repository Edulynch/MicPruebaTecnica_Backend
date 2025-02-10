package com.blautech.pruebaTecnica.demo.api.order.controller;

import com.blautech.pruebaTecnica.demo.api.order.model.Order;
import com.blautech.pruebaTecnica.demo.api.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Inicia un pedido a partir del carrito, asignándole la dirección de envío.
     */
    @PostMapping("/initiate")
    public ResponseEntity<Order> initiateOrder(@RequestParam String shippingAddress) {
        Order order = orderService.initiateOrder(shippingAddress);
        return ResponseEntity.ok(order);
    }

    /**
     * Permite actualizar la dirección de envío de un pedido en estado PENDING.
     */
    @PutMapping("/{orderId}/shipping-address")
    public ResponseEntity<Order> updateShippingAddress(
            @PathVariable Long orderId,
            @RequestParam String newShippingAddress) {
        Order order = orderService.updateShippingAddress(orderId, newShippingAddress);
        return ResponseEntity.ok(order);
    }

    /**
     * Confirma un pedido en estado PENDING, asignando un número de orden y limpiando el carrito.
     */
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long orderId) {
        Order order = orderService.confirmOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Cancela un pedido en estado PENDING. Este endpoint debe invocar la lógica de
     * cancelación (por ejemplo, estableciendo el estado a CANCELLED) y, opcionalmente,
     * limpiar el carrito si fuera necesario.
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Retorna la lista de órdenes del usuario actual (o de todos si el usuario tiene rol WORKER/ADMIN).
     */
    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> orders = orderService.getOrdersForCurrentUser();
        return ResponseEntity.ok(orders);
    }

    /**
     * Retorna el detalle de una orden específica.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderDetail(@PathVariable Long orderId) {
        Order order = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok(order);
    }
}
