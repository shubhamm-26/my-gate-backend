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

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/flat")
public class FlatController {

    private final FlatService flatService;

    @Autowired
    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }

    @PostMapping("/{regionId}/{societyId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId, 'FLAT_ADD')")
    public ResponseEntity<?> addFlat(@RequestBody FlatRequest flatRequest,@PathVariable String regionId, @PathVariable String societyId) {
        try{
            Flat flat = new Flat();
            flat.setFlatNumber(flatRequest.getFlatNumber());
            flat.setOwnerId(flatRequest.getOwnerId());
            flat.setResidentsList(flatRequest.getResidentsList());
            Optional<Flat> flatOptional = flatService.addFlat(flat,regionId,societyId);
            if(flatOptional.isEmpty()){
                return ResponseEntity.badRequest().body("Error adding flat.");
            }else{
                return ResponseEntity.ok().body(flatOptional.get());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding flat: " + e.getMessage());
        }
    }

    @GetMapping("/{regionId}/{societyId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId, 'FLAT_READ_ALL')")
    public ResponseEntity<?> getAllFlats(@PathVariable String regionId, @PathVariable String societyId) {
        try{
            return ResponseEntity.ok(flatService.getAllFlats(regionId,societyId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching flats: " + e.getMessage());
        }
    }

    @PutMapping("/addResident/{regionId}/{societyId}/{flatId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId+'_'+#flatId, 'FLAT_ADD_RESIDENT')")
    public ResponseEntity<?> addResident(@PathVariable String regionId, @PathVariable String societyId, @PathVariable String flatId, @RequestBody Map<String,String> req) {
        try{
            flatService.addResident(regionId,societyId,flatId,req.get("residentId"));
            return ResponseEntity.ok().body("Resident added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding resident: " + e.getMessage());
        }
    }

    @PutMapping("/addOwner/{regionId}/{societyId}/{flatId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId+'_' + #flatId, 'FLAT_ADD_OWNER')")
    public ResponseEntity<?> addOwner(@PathVariable String regionId, @PathVariable String societyId, @PathVariable String flatId, @RequestBody Map<String, String> req) {
        String ownerId = req.get("ownerId");
        try{
            Flat flat = flatService.addOwner(regionId,societyId,flatId,ownerId);
            return ResponseEntity.ok().body("Owner added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding owner: " + e.getMessage());
        }
    }

}
