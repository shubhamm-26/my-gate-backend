package com.mygate.my_gate_backend.model;

import com.mygate.my_gate_backend.model.enums.RolesEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class UserRole {
    private RolesEnum rolesEnum;
    private String referenceId;
}
