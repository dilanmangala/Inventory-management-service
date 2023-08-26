package com.qortex.inventory.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseList {

    private String warehouseName;
    private String warehouseCode;
    private String warehouseType;
    private String assignedId;
    private String statusCode;


}
