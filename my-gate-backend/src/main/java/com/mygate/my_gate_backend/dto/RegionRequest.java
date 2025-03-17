package com.mygate.my_gate_backend.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class RegionRequest {
    @Id
    private String id;
    private String name;
    private String regionId;

}
