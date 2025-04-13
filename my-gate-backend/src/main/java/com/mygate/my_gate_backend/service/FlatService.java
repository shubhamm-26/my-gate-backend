package com.mygate.my_gate_backend.service;

import com.mygate.my_gate_backend.model.*;
import com.mygate.my_gate_backend.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FlatService {

    private final MongoTemplate mongoTemplate;
    private final RegionRepository regionRepository;
    private final SocietyService societyService;
    private final UserService userService;

    public FlatService(MongoTemplate mongoTemplate, RegionRepository regionRepository, SocietyService societyService, UserService userService) {
        this.mongoTemplate = mongoTemplate;
        this.regionRepository = regionRepository;
        this.societyService = societyService;
        this.userService = userService;
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
        Set<String> residents = new HashSet<>();
        flat.setResidentsList(residents);
        flat.setFlatId(generateFlatId(regionId,societyId));
        String collectionName = regionId + "_" + societyId;
        mongoTemplate.save(flat, collectionName);
        return Optional.of(flat);
    }

    public Optional<Flat> findFlatById(String regionId, String societyId, String flatId) {
        String collectionName = regionId + "_" + societyId;
        Flat flat = mongoTemplate.findOne(
                new Query(Criteria.where("flatId").is(flatId)),
                Flat.class,
                collectionName
        );
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


    public List<Flat> getAllFlats(String regionId, String societyId) {
        Optional<Society> societyOptional = societyService.getSocietyBySocId(regionId, societyId);
        if (societyOptional.isEmpty()) {
            throw new IllegalArgumentException("Society with ID " + societyId + " not found in region " + regionId);
        }
        String collectionName = regionId + "_" + societyId;
        List<Flat> flats = mongoTemplate.findAll(Flat.class, collectionName);
        return flats;
    }



    public Flat addResident(String regionId,String societyId,String flatId,String residentId){
        Optional<Flat> flatOptional = findFlatById(regionId, societyId, flatId);
        if(flatOptional.isEmpty()){
            throw new IllegalArgumentException("Flat with ID " + flatId + " not found in society " + societyId);
        }
        Optional<User> userOptional = userService.getUserById(residentId);
        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("User with ID " + residentId + " not found.");
        }
        UserRole userRole = new UserRole();
        userRole.setRole("RESIDENT");
        userRole.setReferenceId(regionId + "_" + societyId + "_" + flatId);
        userService.addRole(residentId,userRole);

        Flat flat = flatOptional.get();
        Set<String> residents = flat.getResidentsList();
        residents.add(residentId);
        System.out.println(residents);

        flat.setResidentsList(residents);
        String collectionName = regionId + "_" + societyId;

        mongoTemplate.save(flat, collectionName);
        return flat;
    }

    public Flat addOwner(String regionId,String societyId,String flatId,String ownerId){
        Optional<Flat> flatOptional = findFlatById(regionId, societyId, flatId);
        if(flatOptional.isEmpty()){
            throw new IllegalArgumentException("Flat with ID " + flatId + " not found in society " + societyId);
        }
        Optional<User> userOptional = userService.getUserById(ownerId);
        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("User with ID " + ownerId + " not found.");
        }
        UserRole userRole = new UserRole();
        userRole.setRole("OWNER");
        userRole.setReferenceId(regionId + "_" + societyId + "_" + flatId);
        userService.addRole(ownerId,userRole);

        Flat flat = flatOptional.get();
        flat.setOwnerId(userOptional.get().getId());
        String collectionName = regionId + "_" + societyId;
        mongoTemplate.save(flat, collectionName);
        return flat;
    }
}
