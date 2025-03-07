package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.model.Region;
import com.mygate.my_gate_backend.service.RegionService;
import com.mygate.my_gate_backend.util.CustomGrantedAuthority;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/region")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @PostMapping("/addRegion")
    public ResponseEntity<?> addRegion(@RequestBody Map<String, String> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        boolean isSuperAdmin = authentication.getAuthorities().stream()
                .anyMatch (auth -> auth instanceof CustomGrantedAuthority customAuth
                        && customAuth.getAuthority().equals("SUPER_ADMIN"));

        if (!isSuperAdmin) {
            return ResponseEntity.status(403).body("Access Denied: Only Super Admin can add a region.");
        }

        authentication.getAuthorities().forEach(auth -> {
            if (auth instanceof CustomGrantedAuthority customAuth) {
                System.out.println("Role: " + customAuth.getAuthority() + ", ReferenceID: " + customAuth.getReferenceId());
            }
        });

        String regionName = requestBody.get("name");

        if (regionName == null || regionName.isEmpty()) {
            return ResponseEntity.badRequest().body("Region name is required.");
        }

        Optional<Region> existingRegion = regionService.getRegionByName(regionName);
        if (existingRegion.isPresent()) {
            return ResponseEntity.badRequest().body("Region with name " + regionName + " already exists.");
        }

        Region savedRegion = regionService.createRegion(regionName);
        return ResponseEntity.ok(savedRegion);
    }
}
