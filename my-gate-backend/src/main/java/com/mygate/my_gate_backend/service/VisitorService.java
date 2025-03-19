package com.mygate.my_gate_backend.service;

import com.mygate.my_gate_backend.dto.VisitorRequest;
import com.mygate.my_gate_backend.model.Society;
import com.mygate.my_gate_backend.model.Visitors;
import com.mygate.my_gate_backend.model.enums.VisitorStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {

    private final SocietyService societyService;
    private final MongoTemplate mongoTemplate;

    public VisitorService(SocietyService societyService, MongoTemplate mongoTemplate) {
        this.societyService = societyService;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Visitors> getAllVisitors(String regionId, String societyId) {
        Optional<Society> societyOptional = societyService.getSocietyBySocId(regionId, societyId);
        if (societyOptional.isEmpty()) {
            throw new IllegalArgumentException("Society with ID " + societyId + " not found in region " + regionId);
        }
        String collectionName = regionId + "_" + societyId + "_visitors";
        return mongoTemplate.findAll(Visitors.class, collectionName);
    }

    public List<Visitors> getVisitorsByFlat(String regionId, String societyId, String flatId) {
        Optional<Society> societyOptional = societyService.getSocietyBySocId(regionId, societyId);
        if (societyOptional.isEmpty()) {
            throw new IllegalArgumentException("Society with ID " + societyId + " not found in region " + regionId);
        }
        String collectionName = regionId + "_" + societyId + "_visitors";
        Query query = new Query();
        query.addCriteria(Criteria.where("flatId").is(flatId));
        return mongoTemplate.find(query, Visitors.class, collectionName);
    }

    public Visitors addVisitor(String regionId, String societyId,String flatId, VisitorRequest visitorRequest) {
        Optional<Society> societyOptional = societyService.getSocietyBySocId(regionId, societyId);
        if (societyOptional.isEmpty()) {
            throw new IllegalArgumentException("Society with ID " + societyId + " not found in region " + regionId);
        }
        String collectionName = regionId + "_" + societyId + "_visitors";
        Visitors visitor = new Visitors();
        visitor.setFlatId(flatId);
        visitor.setName(visitorRequest.getName());
        visitor.setMobile(visitorRequest.getMobile());
        visitor.setPurpose(visitorRequest.getPurpose());
        visitor.setVehicleNumber(visitorRequest.getVehicleNumber());
        visitor.setStatus(VisitorStatus.PENDING);
        mongoTemplate.save(visitor, collectionName);
        return visitor;
    }

    public void updateStatus(String regionId, String societyId, String visitorId, VisitorStatus status) {
        Optional<Society> societyOptional = societyService.getSocietyBySocId(regionId, societyId);
        if (societyOptional.isEmpty()) {
            throw new IllegalArgumentException("Society with ID " + societyId + " not found in region " + regionId);
        }
        String collectionName = regionId + "_" + societyId + "_visitors";
        Query query = new Query();
        query.addCriteria(Criteria.where("visitorId").is(visitorId));
        Visitors visitor = mongoTemplate.findOne(query, Visitors.class, collectionName);
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor with ID " + visitorId + " not found in society " + societyId);
        }
        visitor.setStatus(status);
        mongoTemplate.save(visitor, collectionName);
    }

    public void deleteVisitor(String regionId, String societyId, String visitorId) {
        Optional<Society> societyOptional = societyService.getSocietyBySocId(regionId, societyId);
        if (societyOptional.isEmpty()) {
            throw new IllegalArgumentException("Society with ID " + societyId + " not found in region " + regionId);
        }
        String collectionName = regionId + "_" + societyId + "_visitors";
        Query query = new Query();
        query.addCriteria(Criteria.where("visitorId").is(visitorId));
        mongoTemplate.remove(query, Visitors.class, collectionName);
    }

    public Optional<Visitors> getVisitorById(String regionId, String societyId, String visitorId) {
        String collectionName = regionId + "_" + societyId + "_visitors";
        Visitors visitor = mongoTemplate.findOne(
                new Query(Criteria.where("visitorId").is(visitorId)),
                Visitors.class,
                collectionName
        );
        return Optional.ofNullable(visitor);
    }

    public Optional<Visitors> updateVisitor(String regionId,String societyId,String visitorId,VisitorRequest visitorRequest){
        String collectionName = regionId + "_" + societyId + "_visitors";
        System.out.println(visitorRequest);
        System.out.println(visitorId);
        Visitors visitor = mongoTemplate.findById(visitorId, Visitors.class, collectionName);
        if(visitor == null){
            return Optional.empty();
        }
        visitor.setName(visitorRequest.getName());
        visitor.setMobile(visitorRequest.getMobile());
        visitor.setPurpose(visitorRequest.getPurpose());
        visitor.setVehicleNumber(visitorRequest.getVehicleNumber());
        visitor.setStatus(VisitorStatus.valueOf(visitorRequest.getStatus()));
        System.out.println(visitor);
        mongoTemplate.save(visitor, collectionName);
        return Optional.of(visitor);
    }
}
