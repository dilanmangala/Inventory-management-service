package com.qortex.inventory.dto;


import com.qortex.inventory.model.PurchaseOrder;
import org.springframework.data.domain.Page;

public record PurchaseOrderListResponseRecord(ServiceApiResponse serviceApiResponse,
                                              Page<PurchaseOrder> purchaseOrders) {
}
