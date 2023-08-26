package com.qortex.inventory.common.utils;

import com.qortex.inventory.dto.ServiceApiResponse;
import com.qortex.inventory.dto.ApiContextResponse;

import java.util.Date;

public class SetApiContext {

	public SetApiContext() {
		super();
	}

	public static ServiceApiResponse setApiContext(String tenantId, String channelId, String customerId) {
		ApiContextResponse apiContextResponse = new ApiContextResponse();
		apiContextResponse.setTenantId(tenantId);
		apiContextResponse.setChannelId(channelId);
		apiContextResponse.setCustomerId(customerId);
		apiContextResponse.setTimeStamp(new Date().toString());
		ServiceApiResponse serviceApiResponse = new ServiceApiResponse();
		serviceApiResponse.setApiContextResponse(apiContextResponse);
		return serviceApiResponse;
	}
}
