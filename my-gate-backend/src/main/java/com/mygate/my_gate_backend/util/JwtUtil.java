package com.mygate.my_gate_backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygate.my_gate_backend.model.UserRole;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateToken(String email, Set<UserRole> userRoleSet) {
        try {
            List<Map<String, String>> rolesWithReferences = RoleUtil.sortRoles(userRoleSet).stream()
                    .map(userRole -> {
                        Map<String, String> roleMap = new HashMap<>();
                        roleMap.put("role", userRole.getRolesEnum().name());
                        roleMap.put("reference_id", userRole.getReferenceId());
                        return roleMap;
                    })
                    .toList();
            return Jwts.builder()
                    .setSubject(email)
                    .claim("roles", rolesWithReferences) // Store as a list of role-reference objects
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            if(token == null) {
                return false;
            }
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
                .getBody().getSubject();
    }


    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
                .getBody().getExpiration();
    }

    public List<Map<String, String>> getRolesFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
                .getBody().get("roles", List.class); // Retrieve list of role-reference objects
    }

}
