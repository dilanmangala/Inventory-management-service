package com.qortex.inventory.dto;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@RedisHash(value = "SessionPayload", timeToLive = 300)
public class SessionPayload {

	private boolean authenticated = false;
	private String userId;
	private String username;
	private String fullName;
	private String emailID;
	private Date expiryDate;
	private String roleId;

}
