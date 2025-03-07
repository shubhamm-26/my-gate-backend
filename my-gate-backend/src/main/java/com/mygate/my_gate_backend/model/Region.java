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

@Data
@Document(collection = "regions")
public class Region {
    @Id
    private String id;
    private String name;
    private String regionID;

    @CreatedDate
    @Field(targetType = FieldType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @LastModifiedDate
    @Field(targetType = FieldType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant updatedAt;
}
