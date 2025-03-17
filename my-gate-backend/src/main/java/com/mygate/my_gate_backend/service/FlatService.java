package com.mygate.my_gate_backend.service;

import com.mygate.my_gate_backend.model.Flat;
import com.mygate.my_gate_backend.model.Region;
import com.mygate.my_gate_backend.model.Society;
import com.mygate.my_gate_backend.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlatService {

    private final MongoTemplate mongoTemplate;
    private final RegionRepository regionRepository;
    private final SocietyService societyService;

    @Autowired
    public FlatService(MongoTemplate mongoTemplate, RegionRepository regionRepository, SocietyService societyService) {
        this.mongoTemplate = mongoTemplate;
        this.regionRepository = regionRepository;
        this.societyService = societyService;
    }

    public Optional<Flat> addFlat(Flat flat, String regionId, String societyId) {
        Optional<Region> regionOptional = regionRepository.findByRegionId(regionId);
        if (regionOptional.isEmpty()) {
            throw new IllegalArgumentException("Region with ID " + regionId + " not found.");
        }
        Optional<Society> societyOptional = societyService.getSocietyBySocId(regionId, societyId);
        if (societyOptional.isEmpty()) {
            throw new IllegalArgumentException("Society with ID " + societyId + " not found in region " + regionId);
        }

        Optional<Flat> existingFlat = findFlatByFlatNumber(regionId, societyId, flat.getFlatNumber());
        if (existingFlat.isPresent()) {
            throw new IllegalArgumentException("Flat with number " + flat.getFlatNumber() + " already exists in society " + societyId);
        }
        flat.setFlatId(generateFlatId(regionId,societyId));
        String collectionName = regionId + "_" + societyId;
        mongoTemplate.save(flat, collectionName);
        return Optional.of(flat);
    }

    public Optional<Flat> findFlatById(String regionId, String societyId, String flatId) {
        String collectionName = regionId + "_" + societyId;
        Flat flat = mongoTemplate.findById(flatId, Flat.class, collectionName);
        return Optional.ofNullable(flat);
    }

    public Optional<Flat> findFlatByFlatNumber(String regionId, String societyId, String flatNumber) {
        String collectionName = regionId + "_" + societyId;
        Flat flat = mongoTemplate.findOne(
                new Query(Criteria.where("flatNumber").is(flatNumber)),
                Flat.class,
                collectionName
        );
        return Optional.ofNullable(flat);
    }

    private String generateFlatId(String regionId, String societyId) {
        String collectionName = regionId + "_" + societyId;
        List<Flat> flatList = getAllFlats(regionId,societyId);
        int maxId = 0;
        for (Flat flat : flatList) {
            String flatId = flat.getFlatId();
            String[] parts = flatId.split("FLAT");
            int id = Integer.parseInt(parts[1]);
            if (id > maxId) {
                maxId = id;
            }
        }
        return "FLAT" + (maxId + 1);
    }

    public List<Flat> getAllFlats(String regionId,String societyId){
        Optional<Society> societyOptional = societyService.getSocietyBySocId(regionId, societyId);
        if(societyOptional.isEmpty()){
            throw new IllegalArgumentException("Society with ID " + societyId + " not found in region " + regionId);
        }
        String collectionName = regionId + "_" + societyId;
        return mongoTemplate.findAll(Flat.class, collectionName);
    }
}
