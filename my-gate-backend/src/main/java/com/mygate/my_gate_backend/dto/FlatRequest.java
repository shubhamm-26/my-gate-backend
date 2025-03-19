package com.mygate.my_gate_backend.dto;

import com.mygate.my_gate_backend.model.User;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class FlatRequest {
    private String flatNumber;
    private String ownerId;
    private Set<String> residentsList;
}
