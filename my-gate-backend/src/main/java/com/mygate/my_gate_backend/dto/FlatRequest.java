package com.mygate.my_gate_backend.dto;

import com.mygate.my_gate_backend.model.User;
import lombok.Data;

import java.util.List;

@Data
public class FlatRequest {
    private String flatNumber;
    private User ownerId;
    private List<User> residentsList;
    private String regionId;
    private String societyId;
}
