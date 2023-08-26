package com.qortex.inventory.dto;

import java.math.BigDecimal;

public record PurchaseOrderProductItemRecord(
        String productName,
        String productDescription,
        BigDecimal unitPrice,
        String prodId,
        Integer quantity,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal totalPrice,
        Integer rate,
        String statusCode) {
}
