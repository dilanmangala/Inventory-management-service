package com.qortex.inventory.dto;

import jakarta.validation.constraints.NotEmpty;


public record WarehouseRequest(
		@NotEmpty(message = "Warehouse name is mandatory") String warehouseName,
		@NotEmpty(message = "Warehouse code is mandatory") String warehouseCode,
		@NotEmpty(message = "Warehouse type is mandatory") String warehouseType,
		String assignedId,
		String doorName,
		String buildingName,
		String street,
		String city,
		String state,
		String country,
		String zipCode,
		@NotEmpty(message = "Status code is mandatory")
		String warehouseStatusCode,
		String warehouseAddressStatusCode) {
}

