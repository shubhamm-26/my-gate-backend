package com.mygate.my_gate_backend.service;

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
public class SocietyService {

    private MongoTemplate mongoTemplate;
    private RegionRepository regionRepository;

    @Autowired
    public SocietyService(MongoTemplate mongoTemplate, RegionRepository regionRepository) {
        this.mongoTemplate = mongoTemplate;
        this.regionRepository = regionRepository;
    }

    public Society addSociety(Society society, String regionId) {
        System.out.println(society);
        Optional<Region> regionOptional = regionRepository.findByRegionId(regionId);
        if (regionOptional.isEmpty()) {
            throw new IllegalArgumentException("Region ID '" + regionId + "' does not exist.");
        }
        int count = generateSocId(regionId);
        String societyCollection = regionId + "_SOC" + count;
        String societyId = "SOC" + count;
        society.setSocietyId(societyId);

        mongoTemplate.createCollection(societyCollection);
        mongoTemplate.createCollection(societyCollection+"_visitors");
        mongoTemplate.save(society, regionId+"_society");

        return society;
    }

    private int generateSocId(String regionId) {
        List<Society> societies = getAllSocieties(regionId);
        System.out.println(societies);
        int maxId = 0;
        for (Society society : societies) {
            String societyId = society.getSocietyId();
            String[] parts = societyId.split("SOC");
            int id = Integer.parseInt(parts[1]);
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }

    public List<Society> getAllSocieties(String regionId) {
        return mongoTemplate.findAll(Society.class, regionId+"_society");
    }

    public Optional<Society> getSocietyBySocId(String regionId, String societyId) {
        Query query = new Query().addCriteria(Criteria.where("societyId").is(societyId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Society.class, regionId+"_society"));
    }


}
