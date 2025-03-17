package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.dto.RegionRequest;
import com.mygate.my_gate_backend.model.Region;
import com.mygate.my_gate_backend.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> addRegion(@RequestBody RegionRequest regionRequest) {
        String regionName = regionRequest.getName();

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

    @GetMapping()
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> getAllRegions(){
        Optional<List<Region>> regions = regionService.getAllRegions();
        if(regions.isPresent()){
            return ResponseEntity.ok().body(regions);
        }else{
            return ResponseEntity.badRequest().body("No regions found");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN') || hasAuthority('REGION_ADMIN')")
    public ResponseEntity<?> getRegionById(@PathVariable String id){
        Optional<Region> regionOptional = regionService.getRegionById(id);
        if(regionOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Region with id " + id + " not found.");
        }else{
            return  ResponseEntity.ok().body(regionOptional.get());

        }

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> updateRegion(@PathVariable String id,@RequestBody RegionRequest regionRequest){
        String name = regionRequest.getName();
        Optional<Region> updatedRegion = regionService.updateRegion(id, name);
        if(updatedRegion.isPresent()){
            return ResponseEntity.ok().body(updatedRegion.get());
        }else{
            return ResponseEntity.badRequest().body("Region with id " + id + " not found.");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> deleteRegion(@PathVariable String id){
        try{
            regionService.deleteRegion(id);
            return ResponseEntity.ok().body("Region deleted successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error deleting region: " + e.getMessage());
        }
    }
}
