package com.qortex.inventory.common.token;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JWTTokenProps {
	private String secret;
	private long jwtExpirationInMs;
	private long refreshTokenExpirationInMs;
	private String refreshTokenSecret;

}
