package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.dto.FlatRequest;
import com.mygate.my_gate_backend.model.Flat;
import com.mygate.my_gate_backend.service.FlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.mygate.my_gate_backend.util.SecurityUtil.hasAuthorityWithReference;

@Controller
@RequestMapping("/flat")
public class FlatController {

    private final FlatService flatService;

    @Autowired
    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('SUPER_ADMIN') || hasAuthority('REGION_ADMIN') || hasAuthority('SOC_ADMIN')")
    public ResponseEntity<?> addFlat(@RequestBody FlatRequest flatRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String regionId = flatRequest.getRegionId();
        String societyId = flatRequest.getSocietyId();

        if (!hasAuthorityWithReference(authentication, "SOC_ADMIN",regionId + "_" +societyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to add a flat in this society.");
        }
        try{
            Flat flat = new Flat();
            flat.setFlatNumber(flatRequest.getFlatNumber());
            flat.setOwnerId(flatRequest.getOwnerId());
            flat.setResidentsList(flatRequest.getResidentsList());

            flatService.addFlat(flat,regionId,societyId);
            return ResponseEntity.ok().body("Flat added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding flat: " + e.getMessage());
        }
    }

    @GetMapping("/{regionId}/{societyId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN') || hasAuthority('REGION_ADMIN') || hasAuthority('SOC_ADMIN')")
    public ResponseEntity<?> getAllFlats(@PathVariable String regionId, @PathVariable String societyId) {
        try{
            return ResponseEntity.ok(flatService.getAllFlats(regionId,societyId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching flats: " + e.getMessage());
        }
    }

}
