package com.qortex.inventory.dto;

import lombok.Data;

@Data
public class RefreshTokenResponse {
	private String accessToken;
	private String refreshToken;
	private String errorMessage;
}
