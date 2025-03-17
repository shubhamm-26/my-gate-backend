package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.dto.AddRoleDto;
import com.mygate.my_gate_backend.model.UserRole;
import com.mygate.my_gate_backend.model.enums.RolesEnum;
import com.mygate.my_gate_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.mygate.my_gate_backend.util.SecurityUtil.canAssignRole;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addRole")
    public ResponseEntity<?> addRole(@RequestBody AddRoleDto addRoleDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId= addRoleDto.getId();
        String role = addRoleDto.getRole();
        String referenceId = addRoleDto.getReferenceId();

        UserRole userRole = new UserRole();
        userRole.setRolesEnum(RolesEnum.valueOf(role));
        userRole.setReferenceId(referenceId);

        if (userId == null || role == null || referenceId==null) {
            return ResponseEntity.badRequest().body("User ID and Role are required.");
        }
        try {
            if(canAssignRole(authentication,userRole)){
                userService.addRole(userId, userRole);
                return ResponseEntity.ok().body("Role added successfully.");
            }else{
                return ResponseEntity.status(403).body("Access Denied: You are not authorized to assign this role.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding role."+e.getMessage());
        }

    }


}
