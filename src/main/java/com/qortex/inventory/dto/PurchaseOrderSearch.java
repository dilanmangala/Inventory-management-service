package com.qortex.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;


@Data
@AllArgsConstructor
public class PurchaseOrderSearch {
    private String statusCode;
    private LocalDate issueDate;

    private  String supplier;

    private String poNumber;

}
