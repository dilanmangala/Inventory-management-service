package com.qortex.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventorySearchCriteria {
    private String warehouseName;
    private String warehouseType;
    private String warehouseCode;
}
