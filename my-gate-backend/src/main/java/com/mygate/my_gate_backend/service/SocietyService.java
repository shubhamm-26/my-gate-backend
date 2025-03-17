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
import java.util.Set;
import java.util.stream.Collectors;

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
        Optional<Region> regionOptional = regionRepository.findByRegionId(regionId);
        if (regionOptional.isEmpty()) {
            throw new IllegalArgumentException("Region ID '" + regionId + "' does not exist.");
        }
        Query query = new Query().addCriteria(Criteria.where("societyId").is(societyId));
        Society society = mongoTemplate.findOne(query, Society.class, regionId+"_society");
        return Optional.ofNullable(society);
    }

    public Optional<Society> updateSociety(String regionId,Society society){
        Query query = new Query().addCriteria(Criteria.where("societyId").is(society.getSocietyId()));
        Society existingSociety = mongoTemplate.findOne(query, Society.class, regionId+"_society");
        if(existingSociety == null){
            return Optional.empty();
        }
        existingSociety.setName(society.getName());
        existingSociety.setAddress(society.getAddress());
        mongoTemplate.save(existingSociety, regionId+"_society");
        return Optional.of(existingSociety);
    }

    public void deleteSociety(String regionId, String societyId){
        Optional<Society> societyOptional = getSocietyBySocId(regionId, societyId);
        if(societyOptional.isEmpty()){
            throw new IllegalArgumentException("Society ID '" + societyId + "' does not exist.");
        }
        Set<String> collections = mongoTemplate.getCollectionNames().stream()
                .filter(name->name.contains(societyId))
                .collect(Collectors.toSet());

        collections.forEach(mongoTemplate::dropCollection);

        Query query = new Query().addCriteria(Criteria.where("societyId").is(societyId));
        mongoTemplate.remove(query, Society.class, regionId+"_society");
    }

}
