package com.mygate.my_gate_backend.model.enums;

public enum RolesEnum {
    SUPER_ADMIN(1),
    REGION_ADMIN(2),
    SOC_ADMIN(3),
    OWNER(4),
    RESIDENT(5),
    GUARD(6);

    private final int priority;

    RolesEnum(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
