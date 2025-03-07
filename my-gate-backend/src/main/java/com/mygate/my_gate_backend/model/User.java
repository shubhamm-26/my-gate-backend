package com.mygate.my_gate_backend.model;


import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private Set<UserRole> userRolesSet;

    @CreatedDate
    @Field(targetType = FieldType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @LastModifiedDate
    @Field(targetType = FieldType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant updatedAt;

    public List<UserRole> getSortedRoles() {
        return userRolesSet.stream()
                .sorted(Comparator.comparingInt(role -> role.getRolesEnum().getPriority()))
                .collect(Collectors.toList());
    }
}
