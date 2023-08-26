package com.qortex.inventory.dto;

import lombok.Data;

@Data
public class ApiContextResponse {

	private String customerId;
	private String channelId;
	private String tenantId;
	private String timeStamp;

}
