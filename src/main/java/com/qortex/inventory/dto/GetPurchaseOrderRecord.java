package com.qstsm.framework.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetPurchaseOrderRecord(String purchaseOrderId,
                                     String supplierName,
                                     String supplierCode,
                                     BigDecimal total,
                                     LocalDateTime issueDate,
                                     LocalDateTime receivedDate,
                                     String statusCode

) {
}
