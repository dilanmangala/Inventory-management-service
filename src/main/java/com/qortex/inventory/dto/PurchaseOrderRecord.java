package com.qortex.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record   PurchaseOrderRecord(String entityId,
                                  String supplierId,
                                  LocalDate poIssueDate,
                                  LocalDate poReceivedDate,
                                  String userId,
                                  String warehouseId,
                                  BigDecimal subtotalAmount,
                                  BigDecimal shippingAmount,
                                  BigDecimal tax,
                                  BigDecimal totalAmount,
                                  String note,
                                  String statusCode,
                                  String quotationNumber,
                                  List<PurchaseOrderProductItemRecord> purchaseOrderProductItemsList) {
}
