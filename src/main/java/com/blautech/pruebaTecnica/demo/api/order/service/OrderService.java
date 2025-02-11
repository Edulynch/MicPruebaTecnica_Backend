package com.blautech.pruebaTecnica.demo.api.order.service;

import com.blautech.pruebaTecnica.demo.api.order.model.Order;
import com.blautech.pruebaTecnica.demo.api.order.model.OrderItem;
import com.blautech.pruebaTecnica.demo.api.order.model.OrderStatus;
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

    /**
     * Obtiene el usuario autenticado a partir del token.
     */
    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));
    }

    /**
     * Inicia un pedido a partir del carrito, creando un pedido en estado PENDING.
     *
     * @param shippingAddress Dirección de envío ingresada.
     * @return La orden iniciada.
     */
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
        order.setStatus(OrderStatus.PENDING);  // Asigna el estado inicial PENDING
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
        // No se limpia el carrito hasta que se confirme la orden
        return savedOrder;
    }

    /**
     * Permite actualizar la dirección de envío de un pedido en estado PENDING.
     *
     * @param orderId            ID de la orden.
     * @param newShippingAddress Nueva dirección de envío.
     * @return La orden actualizada.
     */
    @Transactional
    public Order updateShippingAddress(Long orderId, String newShippingAddress) {
        Order order = getOrderForCurrentUser(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede actualizar la dirección de un pedido ya confirmado");
        }
        order.setShippingAddress(newShippingAddress);
        return orderRepository.save(order);
    }

    /**
     * Confirma un pedido en estado PENDING, asigna un número de orden, cambia el estado a CONFIRMED y limpia el carrito.
     *
     * @param orderId ID de la orden a confirmar.
     * @return La orden confirmada.
     */
    @Transactional
    public Order confirmOrder(Long orderId) {
        Order order = getOrderForCurrentUser(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido ya fue confirmado");
        }
        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderNumber(UUID.randomUUID().toString());
        Order savedOrder = orderRepository.save(order);
        ShoppingCart cart = shoppingCartService.getCartForCurrentUser();
        shoppingCartService.clearCart(cart);
        return savedOrder;
    }

    /**
     * Retorna las órdenes del usuario autenticado, o todas si el usuario tiene rol WORKER/ADMIN.
     *
     * @return Lista de órdenes.
     */
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

    /**
     * Retorna el detalle de una orden específica.
     *
     * @param orderId ID de la orden.
     * @return La orden detallada.
     */
    public Order getOrderDetail(Long orderId) {
        return getOrderForCurrentUser(orderId);
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderForCurrentUser(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sólo se pueden cancelar órdenes pendientes");
        }
        order.setStatus(OrderStatus.CANCELLED);
        // Aquí, si se requiere, se puede limpiar el carrito o realizar otras acciones.
        return orderRepository.save(order);
    }

    // MÉTODOS NUEVOS PARA ADMINISTRACIÓN

    // Obtener todas las órdenes (sin restricciones de usuario)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Obtener detalle de una orden sin restricciones (para administración)
    public Order getOrderDetailForAdmin(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
    }

    // Actualizar el estatus de una orden desde el módulo administrativo
    @Transactional
    public Order updateOrderStatusByAdmin(Long orderId, OrderStatus newStatus, User currentUser) {
        Order order = getOrderDetailForAdmin(orderId);
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
        boolean isWorker = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("WORKER"));
        if (isWorker && newStatus == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Worker no puede cancelar órdenes");
        }
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return updatedOrder;
    }

    // Metodo usado para operaciones de usuario normal (por ejemplo, confirmar o cancelar desde el lado de usuario)
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

}
