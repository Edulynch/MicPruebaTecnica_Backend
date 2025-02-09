package com.blautech.pruebaTecnica.demo.api.roles.controller;

import com.blautech.pruebaTecnica.demo.api.roles.dto.UserRoleUpdateRequest;
import com.blautech.pruebaTecnica.demo.api.roles.model.Role;
import com.blautech.pruebaTecnica.demo.api.roles.repository.RoleRepository;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.blautech.pruebaTecnica.demo.api.users.repository.UserRepository;
import com.blautech.pruebaTecnica.demo.audit.service.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditLogService auditLogService;

    public RoleController(UserRepository userRepository, RoleRepository roleRepository, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditLogService = auditLogService;
    }

    // Endpoint para actualizar el rol de un usuario.
    // Solo ADMIN puede usar este endpoint.
    // Se recibe el email y el rol en el body, no como parámetro en la URL.
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user")
    public ResponseEntity<String> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
        // Buscar al usuario mediante email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Buscar el rol solicitado (se asume que se envía en mayúsculas o se convierte a mayúsculas)
        Role newRole = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no encontrado"));

        // Actualizar los roles del usuario (en este ejemplo se reemplaza la colección)
        Set<Role> roles = new HashSet<>();
        roles.add(newRole);
        user.setRoles(roles);

        userRepository.save(user);
        auditLogService.log("ROLE_UPDATE", "Admin", "Actualizó rol para el usuario " + request.getEmail() + " a " + newRole.getName());
        return ResponseEntity.ok("Rol del usuario actualizado correctamente");
    }
}
