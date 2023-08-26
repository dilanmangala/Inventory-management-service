package com.qortex.inventory.dto;

import lombok.Data;

@Data
public class UserTokenResponse {
	private boolean isAuthorized;
	private String message;

}
