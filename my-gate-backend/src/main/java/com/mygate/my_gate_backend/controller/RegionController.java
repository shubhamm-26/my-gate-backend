package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.model.Region;
import com.mygate.my_gate_backend.service.RegionService;
import com.mygate.my_gate_backend.util.CustomGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/region")
public class RegionController {

    private final RegionService regionService;

    @Autowired
    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> addRegion(@RequestBody Map<String, String> requestBody) {
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
