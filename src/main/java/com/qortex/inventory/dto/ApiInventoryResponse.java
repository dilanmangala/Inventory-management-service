package com.qortex.inventory.dto;

import java.util.List;

public record ApiInventoryResponse(ServiceApiResponse serviceApiResponse,
                                   List<InventoryResponse> response) {
}
