package com.qortex.inventory.dto;
import com.qortex.inventory.model.Warehouse;

public record ServiceApiResponseRecord(ServiceApiResponse serviceApiResponse,
                                       Warehouse warehouse) {

}
