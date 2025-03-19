package com.mygate.my_gate_backend.model;

import com.mygate.my_gate_backend.model.enums.VisitorStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class Visitors {
    @Id
    private String id;
    private String name;
    private String mobile;
    private String vehicleNumber;
    private String purpose;
    private VisitorStatus status = VisitorStatus.PENDING;
    private String flatId;

    @CreatedDate
    @Field(targetType = FieldType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant entryTime;

    @Field(targetType = FieldType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant exitTime;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
