package com.blautech.pruebaTecnica.demo.api.users.service;

import com.blautech.pruebaTecnica.demo.api.users.dto.UserProfileDTO;
import com.blautech.pruebaTecnica.demo.api.users.dto.UserProfileUpdateDTO;
import com.blautech.pruebaTecnica.demo.api.users.model.User;
import com.blautech.pruebaTecnica.demo.api.users.repository.UserRepository;
import com.blautech.pruebaTecnica.demo.audit.service.AuditLogService;
import com.blautech.pruebaTecnica.demo.api.roles.model.Role;
import com.blautech.pruebaTecnica.demo.api.roles.repository.RoleRepository;
import com.blautech.pruebaTecnica.demo.util.StatusConstants;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                       AuditLogService auditLogService,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllActiveUsers() {
        return userRepository.findByStatus(StatusConstants.ACTIVE);
    }


    // MÉTODOS NUEVOS PARA USO ADMINISTRATIVO

    // Obtener un usuario por ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // Actualizar un usuario por ID (solo para uso administrativo)
    public User updateUserById(Long id, User updatedData, String performedBy) {
        User existing = getUserById(id);
        existing.setFirstName(updatedData.getFirstName());
        existing.setLastName(updatedData.getLastName());
        existing.setShippingAddress(updatedData.getShippingAddress());
        existing.setBirthDate(updatedData.getBirthDate());
        if (updatedData.getPassword() != null && !updatedData.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }
        User updatedUser = userRepository.save(existing);
        auditLogService.log("UPDATE_ADMIN", performedBy, "Actualizó usuario con id=" + id);
        return updatedUser;
    }

    // Desactivar (eliminar) un usuario por ID
    public void deactivateUserById(Long id, String performedBy) {
        User user = getUserById(id);
        user.setStatus(StatusConstants.INACTIVE);
        userRepository.save(user);
        auditLogService.log("DELETE_ADMIN", performedBy, "Desactivó usuario con id=" + id);
    }

    // Activar un usuario por ID
    public void activateUserById(Long id, String performedBy) {
        User user = getUserById(id);
        user.setStatus(StatusConstants.ACTIVE);
        userRepository.save(user);
        auditLogService.log("ACTIVATE_ADMIN", performedBy, "Activó usuario con id=" + id);
    }

    // Guardar cambios en un usuario (usado para actualizar el rol)
    public User saveUser(User user, String performedBy) {
        User saved = userRepository.save(user);
        auditLogService.log("UPDATE_ADMIN", performedBy, "Actualizó usuario con id=" + user.getId());
        return saved;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public User createUser(User user, String performedBy) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
        }

        // Asignar el rol por defecto "USER" sin importar el payload
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Rol predeterminado no encontrado"));
        user.setRoles(Collections.singleton(defaultRole));

        // Hashear la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        auditLogService.log("CREATE", performedBy, "Creó usuario con email=" + savedUser.getEmail());
        return savedUser;
    }

    public User updateUserByEmail(String email, User userDetails, String performedBy) {
        User existing = getUserByEmail(email);
        // Actualizar campos permitidos
        existing.setFirstName(userDetails.getFirstName());
        existing.setLastName(userDetails.getLastName());
        existing.setShippingAddress(userDetails.getShippingAddress());
        existing.setBirthDate(userDetails.getBirthDate());
        // Actualizar la contraseña si se proporciona
        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        User updated = userRepository.save(existing);
        auditLogService.log("UPDATE", performedBy, "Actualizó usuario con email=" + updated.getEmail());
        return updated;
    }

    public void deleteUserByEmail(String email, String performedBy) {
        User user = getUserByEmail(email);
        user.setStatus(StatusConstants.INACTIVE);
        userRepository.save(user);
        auditLogService.log("DELETE", performedBy, "Desactivó usuario con email=" + user.getEmail());
    }

    // Métodos nuevos para la consulta y actualización del perfil del usuario autenticado

    // Metodo privado para mapear la entidad User a UserProfileDTO
    private UserProfileDTO mapUserToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setShippingAddress(user.getShippingAddress());
        dto.setBirthDate(user.getBirthDate());
        return dto;
    }

    // Obtener el perfil del usuario autenticado
    public UserProfileDTO getProfileForCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return mapUserToProfileDTO(user);
    }

    // Actualizar el perfil del usuario autenticado
    public UserProfileDTO updateProfileForCurrentUser(UserProfileUpdateDTO updateDTO, String performedBy) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        user.setShippingAddress(updateDTO.getShippingAddress());
        user.setBirthDate(updateDTO.getBirthDate());
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        User updatedUser = userRepository.save(user);
        auditLogService.log("UPDATE_PROFILE", performedBy, "Actualizó perfil del usuario con email=" + updatedUser.getEmail());
        return mapUserToProfileDTO(updatedUser);
    }
}
