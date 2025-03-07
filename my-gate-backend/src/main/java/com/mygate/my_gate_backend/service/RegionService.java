package com.mygate.my_gate_backend.service;

import com.mygate.my_gate_backend.model.Region;
import com.mygate.my_gate_backend.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    public Region saveRegion(Region region) {
        return regionRepository.save(region);
    }

    public Region getRegionById(String id) {
        return regionRepository.findById(id).orElse(null);
    }

    private String generateRegionId() {
        return "REG" + (regionRepository.count()+ 1);
    }

    public Region createRegion(String regionName) {
        Region region = new Region();
        String regionId = generateRegionId();

        region.setName(regionName);
        region.setRegionID(regionId);

        Region savedRegion = regionRepository.save(region);

        mongoTemplate.createCollection(regionId);

        return savedRegion;
    }


    public Optional<Region> getRegionByName(String name) {
        return regionRepository.findByName(name);
    }
}
