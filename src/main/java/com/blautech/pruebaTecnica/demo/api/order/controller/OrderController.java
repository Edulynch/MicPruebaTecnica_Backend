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

    // Endpoint para iniciar un pedido a partir del carrito
    @PostMapping("/initiate")
    public ResponseEntity<Order> initiateOrder(@RequestParam String shippingAddress) {
        Order order = orderService.initiateOrder(shippingAddress);
        return ResponseEntity.ok(order);
    }

    // Endpoint para actualizar la dirección de envío de un pedido pendiente
    @PutMapping("/{orderId}/shipping-address")
    public ResponseEntity<Order> updateShippingAddress(@PathVariable Long orderId,
                                                       @RequestParam String newShippingAddress) {
        Order order = orderService.updateShippingAddress(orderId, newShippingAddress);
        return ResponseEntity.ok(order);
    }

    // Endpoint para confirmar un pedido pendiente
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long orderId) {
        Order order = orderService.confirmOrder(orderId);
        return ResponseEntity.ok(order);
    }

    // Endpoint para listar todas las órdenes (según rol)
    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> orders = orderService.getOrdersForCurrentUser();
        return ResponseEntity.ok(orders);
    }

    // Endpoint para obtener el detalle de una orden
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderDetail(@PathVariable Long orderId) {
        Order order = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok(order);
    }
}
