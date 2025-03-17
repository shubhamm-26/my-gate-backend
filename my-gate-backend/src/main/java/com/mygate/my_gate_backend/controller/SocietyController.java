package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.dto.SocietyRequest;
import com.mygate.my_gate_backend.model.Society;
import com.mygate.my_gate_backend.service.SocietyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.mygate.my_gate_backend.util.SecurityUtil.hasAuthorityWithReference;
import static com.mygate.my_gate_backend.util.SecurityUtil.isSuperAdmin;

@RestController
@RequestMapping("/society")
public class SocietyController {

    private final SocietyService societyService;

    @Autowired
    public SocietyController(SocietyService societyService) {
        this.societyService = societyService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN') || hasAuthority('REGION_ADMIN')")
    public ResponseEntity<?> addSociety(@RequestBody SocietyRequest societyRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String regionId = societyRequest.getRegionId();

            if (!hasAuthorityWithReference(authentication, "REGION_ADMIN", regionId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to add a society.");
            }

            Society addSociety = new Society();
            addSociety.setName(societyRequest.getName());
            addSociety.setAddress(societyRequest.getAddress());

            Society newSociety = societyService.addSociety(addSociety, regionId);
            return ResponseEntity.ok(newSociety);


        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding society: " + e.getMessage());
        }
    }
}
