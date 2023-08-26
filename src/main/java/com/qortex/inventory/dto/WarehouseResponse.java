package com.qortex.inventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseResponse {
    private String warehouseId;
    private String warehouseName;
    private String warehouseCode;
    private String warehouseType;
    private String assignedId;
    private String warehouseStatusCode;
    private String doorNo;
    private String buildingName;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zip_code;
    private String assignUserName;

    private String addressStatusCode;

    private LocalDateTime warehouseCreatedOn;

    private String warehouseCreatedBy;

    private LocalDateTime warehouseUpdatedOn;

    private String warehouseUpdatedBy;

    private String warehouseAddressId;

    private LocalDateTime warehouseAddressCreatedOn;

    private String warehouseAddressCreatedBy;

    private LocalDateTime warehouseAddressUpdatedOn;

    private String warehouseAddressUpdatedBy;

}
