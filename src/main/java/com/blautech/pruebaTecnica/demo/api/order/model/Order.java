package com.blautech.pruebaTecnica.demo.api.order.model;

import com.blautech.pruebaTecnica.demo.api.users.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número de orden (se genera al confirmar)
    private String orderNumber;

    // Dirección de envío del pedido
    private String shippingAddress;

    // Usamos el enum para el estado
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    // Metodo auxiliar para obtener el nombre en español del estado
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "";
    }
}
