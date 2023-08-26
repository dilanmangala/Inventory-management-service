package com.qortex.inventory.dto;

public record PurchaseOrderReceiveResponseWithContext(ServiceApiResponse serviceApiResponse,
                                                      PurchaseOrderReceiveResponse purchaseOrderReceiveResponse) {
}
