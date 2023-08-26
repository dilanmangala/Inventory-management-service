package com.qortex.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderReceiveRequest(LocalDateTime received_date, List<ReceivedProducts> received_products_list) {
}
