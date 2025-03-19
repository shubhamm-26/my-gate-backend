package com.mygate.my_gate_backend.repository;

import com.mygate.my_gate_backend.model.RolesPermissions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends MongoRepository<RolesPermissions, String> {}
