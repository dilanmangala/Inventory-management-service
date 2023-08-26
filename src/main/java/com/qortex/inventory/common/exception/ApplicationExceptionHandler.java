package com.qortex.inventory.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler{

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> applicationExceptionHandler(Exception exception){
		Map<Class<? extends Exception>, HttpStatus> exceptionMappings = new HashMap<>();
        exceptionMappings.put(SecurityApplicationException.class, HttpStatus.UNAUTHORIZED);
		exceptionMappings.put(WareHouseDetailsNotFoundException.class, HttpStatus.NOT_FOUND);
		exceptionMappings.put(WarehouseDeletionException.class,HttpStatus.BAD_REQUEST);
		exceptionMappings.put(NotFoundException.class,HttpStatus.NOT_FOUND);
		exceptionMappings.put(MethodArgumentNotValidException.class,HttpStatus.BAD_REQUEST);
		exceptionMappings.put(InventoryNameAlreadyExist.class,HttpStatus.CONFLICT);
		exceptionMappings.put(ResourceNotFoundException.class, HttpStatus.NOT_FOUND);
		exceptionMappings.put(SQLException.class, HttpStatus.INTERNAL_SERVER_ERROR);
		exceptionMappings.put(CommonException.class, HttpStatus.BAD_REQUEST);

		HttpStatus httpStatus = exceptionMappings.getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(httpStatus)
				.body(ProblemDetail.forStatusAndDetail(httpStatus, exception.getMessage()));
	}
}
