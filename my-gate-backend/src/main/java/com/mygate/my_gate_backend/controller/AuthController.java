package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.model.User;
import com.mygate.my_gate_backend.model.UserRole;
import com.mygate.my_gate_backend.service.UserService;
import com.mygate.my_gate_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Email and password are required.");
            }
            if (userService.getUserByEmail(user.getEmail()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
            }
            Set<UserRole> rolesSet = new HashSet<>();
            if (user.getEmail().equals("superadmin@mygate.com")) {
                UserRole userRole = new UserRole();
                userRole.setRole("SUPER_ADMIN");
                userRole.setReferenceId(null);
                rolesSet.add(userRole);
            }
            user.setUserRolesSet(rolesSet);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid email or password.");
        }
        User authenticatedUser = userService.getUserByEmail(user.getEmail());
        Set<UserRole> roles = authenticatedUser.getUserRolesSet();
        String token = jwtUtil.generateToken(user.getEmail(), roles);

        Map<String, Object> response = new HashMap<>();
        response.put("token",token);
        response.put("user",authenticatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
