package com.qortex.inventory.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String errorCode;
    private String errorDescription;
}
