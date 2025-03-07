package com.mygate.my_gate_backend.dto;

import com.mygate.my_gate_backend.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddRoleDTO {

    @Email(message = "Invalid email format")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @NotEmpty(message = "Reference roles cannot be empty")
    private UserRole userRole;
}
