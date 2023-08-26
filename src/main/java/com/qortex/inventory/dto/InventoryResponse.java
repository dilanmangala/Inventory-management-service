package com.qortex.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InventoryResponse {

    private String warehouseId;
    private String warehouseName;
    private String warehouseCode;
    private String warehouseType;
    private String assignedId;
    private String statusCode;
    private String doorName;
    private String buildingName;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

}
