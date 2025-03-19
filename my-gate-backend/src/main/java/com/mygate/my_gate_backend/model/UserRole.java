package com.mygate.my_gate_backend.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class UserRole {
    private String role;
    private String referenceId;

}
