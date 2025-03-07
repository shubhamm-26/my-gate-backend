package com.mygate.my_gate_backend.repository;

import com.mygate.my_gate_backend.model.Region;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RegionRepository extends MongoRepository<Region, String> {
    Optional<Region> findByName(String name);
}
