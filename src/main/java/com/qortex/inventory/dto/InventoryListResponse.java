package com.qortex.inventory.dto;

import com.qortex.inventory.model.Warehouse;
import org.springframework.data.domain.Page;

public record InventoryListResponse(ServiceApiResponse serviceApiResponse,
                                    Page<WarehouseResponse> warehouse) {
}
