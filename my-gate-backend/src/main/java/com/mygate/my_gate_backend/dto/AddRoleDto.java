package com.mygate.my_gate_backend.dto;

import lombok.Data;

@Data
public class AddRoleDto {
    private String id;
    private String role;
    private String referenceId;
}
