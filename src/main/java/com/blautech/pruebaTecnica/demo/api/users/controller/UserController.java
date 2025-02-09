package com.blautech.pruebaTecnica.demo.api.users.controller;

import com.blautech.pruebaTecnica.demo.api.users.dto.UserProfileDTO;
import com.blautech.pruebaTecnica.demo.api.users.dto.UserProfileUpdateDTO;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.blautech.pruebaTecnica.demo.api.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    // Endpoint para listar todos los usuarios (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Endpoint para consultar detalles de un usuario (ADMIN o si el email coincide con el usuario autenticado)
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal")
    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email){
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    // Endpoint para crear un usuario (abierto)
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user,
                                           @RequestParam(name = "performedBy", defaultValue = "Sistema") String performedBy) {
        User created = userService.createUser(user, performedBy);
        return ResponseEntity.ok(created);
    }

    // Endpoint para actualizar un usuario (ADMIN o si el email coincide con el usuario autenticado)
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal")
    @PutMapping("/{email}")
    public ResponseEntity<User> updateUser(@PathVariable String email,
                                           @Valid @RequestBody User userDetails,
                                           @RequestParam(defaultValue = "Sistema") String performedBy){
        User updated = userService.updateUserByEmail(email, userDetails, performedBy);
        return ResponseEntity.ok(updated);
    }

    // Endpoint para desactivar un usuario (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email,
                                           @RequestParam(defaultValue = "Sistema") String performedBy){
        userService.deleteUserByEmail(email, performedBy);
        return ResponseEntity.ok().build();
    }

    // --- Nuevos endpoints para consulta y actualización del perfil del usuario autenticado ---

    // Endpoint para obtener el perfil del usuario autenticado
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile() {
        UserProfileDTO profile = userService.getProfileForCurrentUser();
        return ResponseEntity.ok(profile);
    }

    // Endpoint para actualizar el perfil del usuario autenticado
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @Valid @RequestBody UserProfileUpdateDTO updateDTO,
            @RequestParam(defaultValue = "Sistema") String performedBy) {
        UserProfileDTO updatedProfile = userService.updateProfileForCurrentUser(updateDTO, performedBy);
        return ResponseEntity.ok(updatedProfile);
    }
}
