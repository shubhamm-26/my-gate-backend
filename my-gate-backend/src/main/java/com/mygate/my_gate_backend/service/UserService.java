package com.mygate.my_gate_backend.service;

import com.mygate.my_gate_backend.model.User;
import com.mygate.my_gate_backend.model.UserRole;
import com.mygate.my_gate_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<SimpleGrantedAuthority> authorities = user.getUserRolesSet().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRolesEnum().name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public void addRole(String userId, UserRole userRole) {
        String role = userRole.getRolesEnum().name();
        String referenceId = userRole.getReferenceId();
        if (userId == null || role == null || referenceId == null) {
            throw new IllegalArgumentException("User ID and Role are required.");
        }

        String[] ids = referenceId.split("_");
        String regionId = ids[0];
        String societyId = (ids.length > 1) ? ids[1] : null;
        String flatId = (ids.length > 2) ? ids[2] : null;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userId));
        user.getUserRolesSet().add(userRole);
        userRepository.save(user);
    }
}