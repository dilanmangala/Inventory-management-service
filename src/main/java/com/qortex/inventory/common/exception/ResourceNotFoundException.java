package com.qortex.inventory.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
@AllArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
	private final HttpStatus errorCode;
	private final String message;
}
