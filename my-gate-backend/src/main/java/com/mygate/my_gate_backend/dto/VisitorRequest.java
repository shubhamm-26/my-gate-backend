package com.mygate.my_gate_backend.dto;

import com.mygate.my_gate_backend.model.enums.VisitorStatus;
import lombok.Data;

@Data
public class VisitorRequest {
    private String name;
    private String mobile;
    private String vehicleNumber;
    private String purpose;
    private String status;
}
