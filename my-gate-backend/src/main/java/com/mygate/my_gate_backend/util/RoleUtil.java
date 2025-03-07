package com.mygate.my_gate_backend.util;

import com.mygate.my_gate_backend.model.UserRole;
import com.mygate.my_gate_backend.model.enums.RolesEnum;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleUtil {

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
                .sorted(Comparator.comparingInt(role -> ROLE_PRIORITY.indexOf(role.getRolesEnum())))
                .collect(Collectors.toList());
    }

    public static boolean canAssignRole(RolesEnum assignerRole, String assignerRef, UserRole newRole) {
        RolesEnum newRoleEnum = newRole.getRolesEnum();
        String newRoleRef = newRole.getReferenceId();

        if (assignerRole == RolesEnum.SUPER_ADMIN) {
            return true;
        }

        if (assignerRole == RolesEnum.REGION_ADMIN) {
            return newRoleEnum.getPriority() > assignerRole.getPriority()
                    && (newRoleRef == null || newRoleRef.startsWith(assignerRef));
        }

        if (assignerRole == RolesEnum.SOC_ADMIN) {
            return newRoleEnum.getPriority() > assignerRole.getPriority()
                    && newRoleRef.startsWith(assignerRef);
        }

        return false;
    }
}
