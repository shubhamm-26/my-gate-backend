package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.dto.SocietyRequest;
import com.mygate.my_gate_backend.model.Society;
import com.mygate.my_gate_backend.service.SocietyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/society")
public class SocietyController {

    private final SocietyService societyService;

    @Autowired
    public SocietyController(SocietyService societyService) {
        this.societyService = societyService;
    }

    @PostMapping("/{regionId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId, 'SOCIETY_ADD')")
    public ResponseEntity<?> addSociety(@RequestBody SocietyRequest societyRequest, @PathVariable String regionId) {
        try {
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

    @GetMapping("/{regionId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId, 'SOCIETY_READ_ALL')")
    public ResponseEntity<?> getAllSocieties(@PathVariable String regionId){
        try {
            return ResponseEntity.ok(societyService.getAllSocieties(regionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching societies: " + e.getMessage());
        }
    }

    @GetMapping("/{regionId}/{societyId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId, 'SOCIETY_READ')")
    public ResponseEntity<?> getSocietyById(@PathVariable String regionId,@PathVariable String societyId){
        try {
            Optional<Society> societyOptional = societyService.getSocietyBySocId(regionId, societyId);
            if(societyOptional.isEmpty()){
                return ResponseEntity.badRequest().body("Society with id " + societyId + " not found.");
            }else{
                return  ResponseEntity.ok().body(societyOptional.get());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching society: " + e.getMessage());
        }
    }

    @PutMapping("/{regionId}/{societyId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId, 'SOCIETY_UPDATE')")
    public ResponseEntity<?> updateSociety(@PathVariable String regionId,@PathVariable String societyId,@RequestBody SocietyRequest societyRequest){
        try {
            Society society = new Society();
            society.setName(societyRequest.getName());
            society.setAddress(societyRequest.getAddress());
            society.setSocietyId(societyId);

            Optional<Society> societyOptional = societyService.updateSociety(regionId, society);
            if(societyOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Society with id " + societyId + " not found.");
            }else{
                return ResponseEntity.ok().body(societyOptional.get());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating society: " + e.getMessage());
        }
    }

    @DeleteMapping("/{regionId}/{societyId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId, 'SOCIETY_DELETE')")
    public ResponseEntity<?> deleteSociety(@PathVariable String regionId,@PathVariable String societyId){
        try {
            societyService.deleteSociety(regionId, societyId);
            return ResponseEntity.ok().body("Society with id " + societyId + " deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting society: " + e.getMessage());
        }
    }
}
