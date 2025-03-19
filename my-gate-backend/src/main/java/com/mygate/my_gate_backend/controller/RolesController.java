package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.model.RolesPermissions;
import com.mygate.my_gate_backend.service.RolesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
public class RolesController {

    private final RolesService rolesService;

    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @GetMapping()
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'SUPER_ADMIN', 'ROLE_READ_ALL')")
    public ResponseEntity<?> getAllRoles() {
        Optional<List<RolesPermissions>> rolesPermissionsList = rolesService.getAllRolesPermissions();
        if (rolesPermissionsList.isEmpty()) {
            return ResponseEntity.badRequest().body("No roles found.");
        }else{
            return ResponseEntity.ok().body(rolesPermissionsList.get());
        }
    }

    @PostMapping()
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'SUPER_ADMIN', 'ROLE_ADD')")
    public ResponseEntity<?> addRole(@RequestBody RolesPermissions rolesPermissions) {
        String roleName = rolesPermissions.getId();
        if (roleName == null || roleName.isEmpty()) {
            return ResponseEntity.badRequest().body("Role name is required.");
        }
        Optional<RolesPermissions> existingRole = rolesService.getRolesPermissions(roleName);
        if (existingRole.isPresent()) {
            return ResponseEntity.badRequest().body("Role with name " + roleName + " already exists.");
        }
        Optional<RolesPermissions> savedRole = rolesService.addRole(rolesPermissions);
        return ResponseEntity.ok(savedRole);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'SUPER_ADMIN', 'ROLE_READ')")
    public ResponseEntity<?> getRoleById(@PathVariable String id){
        Optional<RolesPermissions> roleOptional = rolesService.getRolesPermissions(id);
        if(roleOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Role with id " + id + " not found.");
        }else{
            return ResponseEntity.ok().body(roleOptional.get());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'SUPER_ADMIN', 'ROLE_DELETE')")
    public ResponseEntity<?> deleteRole(@PathVariable String id){
        Optional<RolesPermissions> roleOptional = rolesService.getRolesPermissions(id);
        if(roleOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Role with id " + id + " not found.");
        }else{
            rolesService.deleteRolesPermissions(id);
            return ResponseEntity.ok().body("Role with id " + id + " deleted.");
        }
    }

    @PutMapping()
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'SUPER_ADMIN', 'ROLE_UPDATE')")
    public ResponseEntity<?> addPermissions(@RequestBody RolesPermissions rolesPermissions){
        Optional<RolesPermissions> roleOptional = rolesService.getRolesPermissions(rolesPermissions.getId());
        if(roleOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Role with id " + rolesPermissions.getId() + " not found.");
        }else{
            Optional<RolesPermissions> rolesPermissions1 = rolesService.addPermissionsToRole(rolesPermissions.getId(), rolesPermissions.getPermissions());
            if(rolesPermissions1.isEmpty()){
                return ResponseEntity.badRequest().body("Error adding permissions to role.");
            }else{
                return ResponseEntity.ok().body(rolesPermissions1.get());
            }
        }
    }
}
