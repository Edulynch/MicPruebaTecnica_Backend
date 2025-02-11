package com.blautech.pruebaTecnica.demo.api.admin.controller;

import com.blautech.pruebaTecnica.demo.api.order.model.Order;
import com.blautech.pruebaTecnica.demo.api.order.model.OrderStatus;
import com.blautech.pruebaTecnica.demo.api.order.service.OrderService;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.blautech.pruebaTecnica.demo.api.users.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public AdminOrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    // Obtener todas las órdenes (ADMIN y WORKER pueden verlas)
    @PreAuthorize("hasAnyRole('ADMIN','WORKER')")
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Obtener el detalle de una orden
    @PreAuthorize("hasAnyRole('ADMIN','WORKER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderDetail(@PathVariable Long orderId) {
        Order order = orderService.getOrderDetailForAdmin(orderId);
        return ResponseEntity.ok(order);
    }

    // Actualizar el estatus de una orden
    @PreAuthorize("hasAnyRole('ADMIN','WORKER')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId,
                                                   @RequestParam("newStatus") String newStatusStr) {
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de orden inválido");
        }
        // Obtener el usuario autenticado (el principal es el email, según la implementación de JWT)
        String email = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));
        Order updatedOrder = orderService.updateOrderStatusByAdmin(orderId, newStatus, currentUser);
        return ResponseEntity.ok(updatedOrder);
    }
}
