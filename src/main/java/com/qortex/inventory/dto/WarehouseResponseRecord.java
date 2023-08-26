package com.qortex.inventory.dto;


public record WarehouseResponseRecord(
        String warehouseId,
        String warehouseName,
        String warehouseCode,
        String warehouseType,
        String assignedId,
        String warehouseStatusCode,
        String doorNo,
        String buildingName,
        String street,
        String city,
        String state,
        String country,
        String zipCode,
        String assignUserName) {

}
