package com.mygate.my_gate_backend.model;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Set;

@Data
public class Flat {

    @Id
    private String id;
    private String flatNumber;

    private String flatId;

    private String ownerId;

    private Set<String> residentsList;

    @CreatedDate
    @Field(targetType = FieldType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @LastModifiedDate
    @Field(targetType = FieldType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant updatedAt;

    @LastModifiedBy
    private String updatedBy;

    @CreatedBy
    private String createdBy;

}
