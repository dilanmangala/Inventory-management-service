package com.qortex.inventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qortex.inventory.model.Warehouse;
import lombok.Data;

/**
 * @author RAHUL KUSHWAH
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ServiceApiResponse {

	private ApiContextResponse apiContextResponse;

	private Warehouse response;

}