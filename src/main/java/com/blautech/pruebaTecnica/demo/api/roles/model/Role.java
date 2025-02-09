package com.blautech.pruebaTecnica.demo.api.roles.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ejemplo: "ADMIN", "WORKER", "USER"
    @Column(unique = true, nullable = false)
    private String name;

    // Descripción del rol
    @Column(nullable = false)
    private String description;

    // Constructor por defecto
    public Role() {}

    // Constructor que incluye name y description
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
