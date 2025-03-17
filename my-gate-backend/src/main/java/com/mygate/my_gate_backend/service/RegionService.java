package com.mygate.my_gate_backend.service;

import com.mygate.my_gate_backend.model.Region;
import com.mygate.my_gate_backend.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RegionService(RegionRepository regionRepository, MongoTemplate mongoTemplate) {
        this.regionRepository = regionRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<List<Region>> getAllRegions() {
        return Optional.of(regionRepository.findAll());
    }

    public Optional<Region> getRegionById(String id) {
        return regionRepository.findById(id);
    }

    public Optional<Region> getRegionByName(String name) {
        return regionRepository.findByName(name);
    }

    public Optional<Region> findByRegionID(String regionID) { return regionRepository.findByRegionId(regionID);}

    public Region createRegion(String regionName) {
        Region region = new Region();
        List<Region> regions = regionRepository.findAll();
        int maxId = 0;
        for (Region reg : regions) {
            String regionId = reg.getRegionId();
            String[] parts = regionId.split("REG");
            int id = Integer.parseInt(parts[1]);
            if (id > maxId) {
                maxId = id;
            }
        }
        String regionId = "REG" + (maxId + 1);


        region.setName(regionName);
        region.setRegionId(regionId);

        Region savedRegion = regionRepository.save(region);

        mongoTemplate.createCollection(regionId+"_society");

        return savedRegion;
    }

    public Optional<Region> updateRegion(String id, String name) {
        Optional<Region> regionOptional = regionRepository.findById(id);
        if (regionOptional.isEmpty()) {
            return Optional.empty();
        }
        Region region = regionOptional.get();
        region.setName(name);
        return Optional.of(regionRepository.save(region));
    }

    public void deleteRegion(String id) {
        Optional<Region> regionOptional = regionRepository.findById(id);
        if (regionOptional.isEmpty()) {
            throw new IllegalArgumentException("Region with id '" + id + "' does not exist.");
        }else{
            Region region = regionOptional.get();
            String regionId = region.getRegionId();
            Set<String> collections = mongoTemplate.getCollectionNames().stream()
                    .filter(name -> name.startsWith(regionId))
                    .collect(Collectors.toSet());

            collections.forEach(
                    mongoTemplate::dropCollection
            );
            regionRepository.deleteById(id);
        }
    }

}
