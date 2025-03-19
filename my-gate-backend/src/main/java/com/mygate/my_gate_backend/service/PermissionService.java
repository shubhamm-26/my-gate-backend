package com.mygate.my_gate_backend.service;

import com.mygate.my_gate_backend.util.CustomGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    public boolean hasPermission(Authentication authentication, String referenceId, String requiredPermission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        List<CustomGrantedAuthority> authorityList = authentication.getAuthorities().stream()
                .filter(auth -> auth instanceof CustomGrantedAuthority)
                .map(auth -> (CustomGrantedAuthority) auth)
                .toList();

        for (CustomGrantedAuthority authority : authorityList) {
            if ((isReferenceMatching(authority.getReferenceId(), referenceId) &&
                    authority.getPermissions().contains(requiredPermission)) ||authority.getRole().equals("SUPER_ADMIN")) {
                return true;
            }
        }
        return false;
    }
    private boolean isReferenceMatching(String storedReferenceId, String requestReferenceId) {
        return requestReferenceId.startsWith(storedReferenceId) || "SUPER_ADMIN".equals(storedReferenceId);
    }
}
