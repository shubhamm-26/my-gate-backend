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
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'SUPER_ADMIN', 'REGION_ADD')")
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
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'SUPER_ADMIN', 'REGION_READ_ALL')")
    public ResponseEntity<?> getAllRegions() {
        try {
            List<Region> regions = regionService.getAllRegions();
            return ResponseEntity.ok().body(regions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching regions: " + e.getMessage());
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication,#id , 'REGION_READ')")
    public ResponseEntity<?> getRegionById(@PathVariable String id){
        Optional<Region> regionOptional = regionService.getRegionByRegionId(id);
        if(regionOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Region with id " + id + " not found.");
        }else{
            return  ResponseEntity.ok().body(regionOptional.get());

        }

    }

    @PutMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'SUPER_ADMIN', 'REGION_UPDATE')")
    public ResponseEntity<?> updateRegion(@PathVariable String id,@RequestBody RegionRequest regionRequest){
        try{
            String name = regionRequest.getName();
            Optional<Region> updatedRegion = regionService.updateRegion(id, name);
            if(updatedRegion.isPresent()){
                return ResponseEntity.ok().body(updatedRegion.get());
            }else{
                return ResponseEntity.badRequest().body("Region with id " + id + " not found.");
            }
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Error updating region: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'SUPER_ADMIN', 'REGION_DELETE')")
    public ResponseEntity<?> deleteRegion(@PathVariable String id){
        try{
            regionService.deleteRegion(id);
            return ResponseEntity.ok().body("Region deleted successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error deleting region: " + e.getMessage());
        }
    }
}
