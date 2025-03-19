package com.mygate.my_gate_backend.controller;

import com.mygate.my_gate_backend.dto.VisitorRequest;
import com.mygate.my_gate_backend.service.VisitorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/visitor")
public class VisitorController {

    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @GetMapping("/{regionId}/{societyId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId, 'VISITOR_READ_ALL')")
    public ResponseEntity<?> getAllVisitors(@PathVariable String regionId, @PathVariable String societyId){
        return ResponseEntity.ok().body(visitorService.getAllVisitors(regionId, societyId));
    }

    @GetMapping("/{regionId}/{societyId}/{flatId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId+'_'+#flatId, 'VISITOR_READ')")
    public ResponseEntity<?> getVisitorsByFlat(@PathVariable String regionId, @PathVariable String societyId, @PathVariable String flatId){
        return ResponseEntity.ok().body(visitorService.getVisitorsByFlat(regionId, societyId, flatId));
    }

    @PostMapping("/{regionId}/{societyId}/{flatId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId, 'VISITOR_ADD')")
    public ResponseEntity<?> addVisitor(@RequestBody VisitorRequest visitorRequest, @PathVariable String regionId, @PathVariable String societyId, @PathVariable String flatId){
        return ResponseEntity.ok().body(visitorService.addVisitor(regionId, societyId,flatId, visitorRequest));
    }

    @PutMapping("/{regionId}/{societyId}/{flatId}/{visitorId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, #regionId + '_' + #societyId+'_'+#flatId+'_'+#visitorId, 'VISITOR_UPDATE')")
    public ResponseEntity<?> updateVisitor(@RequestBody VisitorRequest visitorRequest, @PathVariable String regionId, @PathVariable String societyId, @PathVariable String flatId, @PathVariable String visitorId){
        return ResponseEntity.ok().body(visitorService.updateVisitor(regionId, societyId, visitorId, visitorRequest));
    }
}
