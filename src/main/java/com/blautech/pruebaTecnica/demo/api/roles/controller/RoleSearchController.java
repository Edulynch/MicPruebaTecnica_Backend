package com.blautech.pruebaTecnica.demo.api.roles.controller;

import com.blautech.pruebaTecnica.demo.api.roles.model.Role;
import com.blautech.pruebaTecnica.demo.api.roles.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/roles")
public class RoleSearchController {

    private final RoleRepository roleRepository;

    public RoleSearchController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Endpoint para buscar un rol por nombre.
     * Solo accesible para ADMIN.
     * Ejemplo de uso: GET /api/roles/search?name=ADMIN
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Role> getRoleByName(@RequestParam("name") String name) {
        Role role = roleRepository.findByName(name.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
        return ResponseEntity.ok(role);
    }
}
