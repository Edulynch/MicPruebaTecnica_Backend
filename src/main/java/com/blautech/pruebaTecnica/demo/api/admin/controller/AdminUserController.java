package com.blautech.pruebaTecnica.demo.api.admin;

import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.blautech.pruebaTecnica.demo.api.users.service.UserService;
import com.blautech.pruebaTecnica.demo.api.roles.dto.RoleIdRequest;
import com.blautech.pruebaTecnica.demo.api.roles.model.Role;
import com.blautech.pruebaTecnica.demo.api.roles.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminUserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    // Obtener todos los usuarios (ADMIN y WORKER pueden verlos)
    @PreAuthorize("hasAnyRole('ADMIN','WORKER')")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Obtener detalle de un usuario por ID
    @PreAuthorize("hasAnyRole('ADMIN','WORKER')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Actualizar datos de usuario (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User updatedUser,
                                           @RequestParam(name = "performedBy", defaultValue = "Admin") String performedBy) {
        User user = userService.updateUserById(id, updatedUser, performedBy);
        return ResponseEntity.ok(user);
    }

    // Desactivar (eliminar) un usuario (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable("id") Long id,
                                               @RequestParam(name = "performedBy", defaultValue = "Admin") String performedBy) {
        userService.deactivateUserById(id, performedBy);
        return ResponseEntity.ok().build();
    }

    // Activar un usuario (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(@PathVariable("id") Long id,
                                               @RequestParam(name = "performedBy", defaultValue = "Admin") String performedBy) {
        userService.activateUserById(id, performedBy);
        return ResponseEntity.ok("Usuario activado correctamente");
    }

    // Actualizar el rol de un usuario usando RoleIdRequest (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable("id") Long id,
                                                 @RequestBody RoleIdRequest request,
                                                 @RequestParam(name = "performedBy", defaultValue = "Admin") String performedBy) {
        Role newRole = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no encontrado"));
        User user = userService.getUserById(id);
        // Usar una colección mutable para evitar problemas con conjuntos inmutables
        user.setRoles(new HashSet<>());
        user.getRoles().add(newRole);
        userService.saveUser(user, performedBy);
        return ResponseEntity.ok("Rol del usuario actualizado correctamente");
    }
}
