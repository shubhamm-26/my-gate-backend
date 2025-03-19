package com.mygate.my_gate_backend.service;

import com.mygate.my_gate_backend.model.RolesPermissions;
import com.mygate.my_gate_backend.repository.RolePermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RolesService {

    private final RolePermissionRepository rolePermissionRepository;

    public RolesService(RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public Optional<RolesPermissions> addRole(RolesPermissions rolesPermissions) {
        return Optional.of(rolePermissionRepository.save(rolesPermissions));
    }
    public Optional<RolesPermissions> getRolesPermissions(String role) {
        return rolePermissionRepository.findById(role);
    }

    public Optional<List<RolesPermissions>> getAllRolesPermissions() {
        return Optional.of(rolePermissionRepository.findAll());
    }

    public Optional<RolesPermissions> addPermissionsToRole(String role, Set<String> permissions) {
        return rolePermissionRepository.findById(role)
                .map(rolesPermissions -> {
                    rolesPermissions.getPermissions().addAll(permissions);
                    return rolePermissionRepository.save(rolesPermissions);
                });
    }

    public void deleteRolesPermissions(String role) {
        rolePermissionRepository.deleteById(role);
    }

    public boolean hasPermission(String role, String permission) {
        return rolePermissionRepository.findById(role)
                .map(rolesPermissions -> rolesPermissions.getPermissions().contains(permission))
                .orElse(false);
    }

    public boolean hasRole(String role) {
        return rolePermissionRepository.existsById(role);
    }

    public Optional<Set<String>> getPermissionsForRole(String role) {
        return rolePermissionRepository.findById(role)
                .map(RolesPermissions::getPermissions);
    }
}
