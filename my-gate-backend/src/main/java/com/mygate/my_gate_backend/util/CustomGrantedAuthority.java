package com.mygate.my_gate_backend.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class CustomGrantedAuthority implements GrantedAuthority {
    private final String role;
    private final String referenceId;
    private final Set<String> permissions;

    @Override
    public String getAuthority() {
        return role;
    }

    public String getReferenceId() {
        return referenceId;
    }
}
