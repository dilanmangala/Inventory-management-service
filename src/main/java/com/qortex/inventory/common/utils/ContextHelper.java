package com.qortex.inventory.common.utils;

import com.qortex.inventory.dto.ApiContextResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Service
public class ContextHelper {

	public ApiContextResponse prepareApiContextResponse(String tenantId, String channelId, String customerId) {
		ApiContextResponse contextResponse = new ApiContextResponse();
		contextResponse.setTenantId(tenantId);
		contextResponse.setChannelId(channelId);
		contextResponse.setCustomerId(customerId);
		contextResponse.setTimeStamp(DateUtil.getCurrentDateAsString());
		return contextResponse;
	}

	public ApiContextResponse getApiContext() {
		ApiContextResponse contextResponse = new ApiContextResponse();
		HttpServletRequest request = (HttpServletRequest) ((ServletRequestAttributes) Objects
				.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
		contextResponse.setTenantId(request.getHeader("X-TENANT-ID"));
		contextResponse.setChannelId(request.getHeader("X-CHANNEL-ID"));
		contextResponse.setCustomerId(request.getHeader("X-CUSTOMER-ID"));
		contextResponse.setTimeStamp(DateUtil.getCurrentDateAsString());
		return contextResponse;
	}
}
