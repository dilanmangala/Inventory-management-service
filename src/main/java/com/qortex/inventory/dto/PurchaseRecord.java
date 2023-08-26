package com.qortex.inventory.dto;

import com.qortex.inventory.model.PurchaseOrder;

public record PurchaseRecord(ServiceApiResponse serviceApiResponse , PurchaseOrder purchaseOrder) {
}
