package com.qortex.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReceivedProducts(String prod_id,
                               String warehouse_id,
                               Integer quantity_received,
                               boolean check_quantity_flag,
                               List<String> serial_number_list,
                               boolean check_serial_numbers_flag,
                               LocalDateTime expiry_date) {
}
