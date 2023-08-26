package com.qortex.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record UpdatePurchaseOrderRecord(String entityId,
                                        String poNumber,
                                        String supplierId,
                                        LocalDateTime poIssueDate,
                                        LocalDateTime poReceivedDate,
                                        String userId,
                                        String warehouseId,
                                        BigDecimal subtotalAmount,
                                        BigDecimal shippingAmount,
                                        BigDecimal tax,
                                        BigDecimal totalAmount,
                                        String note,

                                        String statusCode,
                                        List<PurchaseOrderProductItemRecord> purchaseOrderProductItemsList) {
}
