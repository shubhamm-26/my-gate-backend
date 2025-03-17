package com.mygate.my_gate_backend.util;

import com.mygate.my_gate_backend.model.UserRole;
import com.mygate.my_gate_backend.model.enums.RolesEnum;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SecurityUtil {

    private static final List<RolesEnum> ROLE_PRIORITY = List.of(
            RolesEnum.SUPER_ADMIN,
            RolesEnum.REGION_ADMIN,
            RolesEnum.SOC_ADMIN,
            RolesEnum.OWNER,
            RolesEnum.RESIDENT,
            RolesEnum.GUARD
    );

    public static List<UserRole> sortRoles(Set<UserRole> roles) {
        return roles.stream()
                .sorted(Comparator.comparingInt(role -> role.getRolesEnum().getPriority()))
                .toList();
    }

    public static boolean canAssignRole(Authentication authentication, UserRole userRole) {
        RolesEnum rolesEnum = userRole.getRolesEnum();
        String referenceId = userRole.getReferenceId();

        if (isSuperAdmin(authentication)) {
            return true;
        }

        return switch (rolesEnum) {
            case SUPER_ADMIN, REGION_ADMIN -> isSuperAdmin(authentication);
            case SOC_ADMIN -> hasAuthorityWithReference(authentication, "REGION_ADMIN", extractRegionId(referenceId));
            case OWNER -> hasAuthorityWithReference(authentication, "SOC_ADMIN", extractSocietyId(referenceId));
            case RESIDENT -> hasAuthorityWithReference(authentication, "OWNER", referenceId) || hasAuthorityWithReference(authentication, "SOC_ADMIN", extractSocietyId(referenceId));
            case GUARD -> hasAuthorityWithReference(authentication, "SOC_ADMIN", extractSocietyId(referenceId));
        };
    }

    public static boolean isSuperAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SUPER_ADMIN"));
    }

    public static boolean hasAuthorityWithReference(Authentication authentication, String role, String referenceId) {
        System.out.println("Checking access for role: " + role + " with referenceId: " + referenceId);
        RolesEnum requiredRole = RolesEnum.valueOf(role);
        return authentication.getAuthorities().stream()
                .filter(auth -> auth instanceof CustomGrantedAuthority)
                .map(auth -> (CustomGrantedAuthority) auth)
                .anyMatch(auth -> {
                    RolesEnum userRole = RolesEnum.valueOf(auth.getAuthority());

                    boolean hasHigherOrEqualPrivilege = userRole.getPriority() <= requiredRole.getPriority();
                    boolean isWithinScope;
                    if(userRole.getPriority()==1){
                        isWithinScope = true;
                    }else {
                        isWithinScope = auth.getReferenceId().equals(referenceId);
                    }
                    return hasHigherOrEqualPrivilege && isWithinScope;
                });
    }



    private static String extractRegionId(String referenceId) {
        return referenceId.split("_")[0];
    }

    private static String extractSocietyId(String referenceId) {
        return referenceId.split("_")[1];
    }

    private static boolean isValidReferenceId(RolesEnum rolesEnum, String referenceId) {
        switch (rolesEnum) {
            case SUPER_ADMIN, REGION_ADMIN:
                return true;
            case SOC_ADMIN,GUARD:
                return referenceId.matches("^REG\\d+_SOC\\d+$");
            case OWNER, RESIDENT:
                return referenceId.matches("^REG\\d+_SOC\\d+_FLAT\\d+$");
            default:
                return false;
        }
    }

}
