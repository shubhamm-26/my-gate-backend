package com.mygate.my_gate_backend.util;

import com.mygate.my_gate_backend.service.PermissionService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PermissionService permissionService;

    public CustomPermissionEvaluator(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        System.out.println("CustomPermissionEvaluator called!");
        System.out.println("Authentication: " + authentication);
        System.out.println("Reference ID: " + targetDomainObject);
        System.out.println("Permission: " + permission);
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (targetDomainObject instanceof String referenceId && permission instanceof String requiredPermission) {
            return permissionService.hasPermission(authentication, referenceId, requiredPermission);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
