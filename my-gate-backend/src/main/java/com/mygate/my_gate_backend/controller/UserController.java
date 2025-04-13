package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.dto.AddRoleDto;
import com.mygate.my_gate_backend.model.User;
import com.mygate.my_gate_backend.model.UserRole;
import com.mygate.my_gate_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication,'SUPER_ADMIN', 'MANAGE_ROLES')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PostMapping("/addRole")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #addRoleDto.referenceId, 'MANAGE_ROLES')")
    public ResponseEntity<?> addRole(@RequestBody AddRoleDto addRoleDto) {

        String userId= addRoleDto.getId();
        String role = addRoleDto.getRole();
        String referenceId = addRoleDto.getReferenceId();

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setReferenceId(referenceId);

        if (userId == null || role == null || referenceId==null) {
            return ResponseEntity.badRequest().body("User ID and Role are required.");
        }
        try {
                Optional<User> user = userService.addRole(userId, userRole);
                if(user.isPresent()) {
                    return ResponseEntity.ok().body(user.get());
                }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding role."+e.getMessage());
        }
    return ResponseEntity.badRequest().body("Error adding role.");}
}
