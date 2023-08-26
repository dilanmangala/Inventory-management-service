package com.qortex.inventory.dto;

import lombok.Data;

@Data
public class TokenValidateRequest {
    private String username;
    private String token;
}
