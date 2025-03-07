package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.dto.AddRoleDTO;
import com.mygate.my_gate_backend.model.UserRole;
import com.mygate.my_gate_backend.model.enums.RolesEnum;
import com.mygate.my_gate_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mygate.my_gate_backend.util.RoleUtil.canAssignRole;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addRole")
    public ResponseEntity<?> addRole(@RequestBody AddRoleDTO addRoleDTO, HttpServletRequest request) {
        List<UserRole> rolesWithReferences = (List<UserRole>) request.getAttribute("rolesWithReferences");
        String userRef = addRoleDTO.getEmail();
        UserRole newRole = addRoleDTO.getUserRole();

        UserRole highestRole = rolesWithReferences.stream()
                .min((r1, r2) -> Integer.compare(r1.getRolesEnum().getPriority(), r2.getRolesEnum().getPriority()))
                .orElse(null);

        if (highestRole == null) {
            return ResponseEntity.status(403).body("Access Denied: User has no valid roles");
        }

        RolesEnum highestRoleEnum = highestRole.getRolesEnum();
        String highestRoleRef = highestRole.getReferenceId();
        if (!canAssignRole(highestRoleEnum, highestRoleRef, newRole)) {
            return ResponseEntity.status(403).body("Access Denied: Insufficient permissions");
        }

        userService.addRoleToUser(userRef, newRole);
        return ResponseEntity.ok("Role added successfully");
    }


}
