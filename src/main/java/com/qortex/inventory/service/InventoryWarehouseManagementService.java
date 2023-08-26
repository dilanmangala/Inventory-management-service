package com.qortex.inventory.service;

import com.qortex.inventory.common.exception.WareHouseDetailsNotFoundException;
import com.qortex.inventory.dto.*;
import com.qortex.inventory.model.PurchaseOrder;
import com.qortex.inventory.model.Warehouse;
import org.springframework.data.domain.Page;



/**
 * @author RAHUL KUSHWAH
 * @version 1.0
 * @since 23-02-2023
 */

public interface InventoryWarehouseManagementService {

	Warehouse addInventoryWarehouse(WarehouseRequest dto, String userId);

	Warehouse updateWareHouse(String id, WarehouseRequest updateRequest, String userId)
			throws WareHouseDetailsNotFoundException;

	String deleteWarehouse(DeleteWarehouse deleteList, String userId);

	WarehouseResponse getWarehouseById(String warehouseType, String id);
	Page<WarehouseResponse> getAllInventories(String warehouseName, String warehouseType,
										 String warehouseCode, Integer offset, Integer pageSize);

	String createPurchaseOrder(PurchaseOrderRecord purchaseOrderRecord, String userId);

	String updatePurchaseOrder(UpdatePurchaseOrderRecord purchaseOrderRecord, String userId, String purchaseOrderId);

	Page<PurchaseOrder> getPurchaseOrders(PurchaseOrderSearch poSearchAttributes, PurchaseOrderPage purchaseOrderPage, PurchaseOrderListSegregator purchaseOrderListSegregator, String userId);

	String deletePurchaseOrder(String purchaseOrderId, String userId);

	PurchaseOrder getPurchaseOrder(String purchaseOrderId, PurchaseOrderListSegregator purchaseOrderListSegregator, String userId);
	PurchaseOrderStatusResponse submitPurchaseOrder(String purchaseOrderId, String userId);

	PurchaseOrderStatusResponse approvePurchaseOrder(String purchaseOrderId, String userId, ApproveOrderRequest approveOrderRequest);

	Page<PurchaseOrder> getPurchaseOrdersBySupplierId(PurchaseOrderSearch poSearchAttributes, PurchaseOrderPage purchaseOrderPage, String supplier_id);

	PurchaseOrderReceiveResponse receivePurchaseOrder(String po_id, PurchaseOrderReceiveRequest purchaseOrderReceiveRequest, String userId);

}
