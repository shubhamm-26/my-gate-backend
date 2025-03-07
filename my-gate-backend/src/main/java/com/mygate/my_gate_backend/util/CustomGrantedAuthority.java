package com.mygate.my_gate_backend.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
@ToString
public class CustomGrantedAuthority implements GrantedAuthority {
    private final String role;
    private final String referenceId;

    @Override
    public String getAuthority() {
        return role;
    }
}
