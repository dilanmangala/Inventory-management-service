package com.qortex.inventory.common.constant;

public class URLConstant {
	public static final String BASE_URL = "/api/v1/inventory-management-service";
	public static final String ADD_WAREHOUSES = "/warehouses";
	public static final String DELETE_WAREHOUSES = "/warehouses";
	public static final String PURCHASE_ORDERS = "/purchase-orders";
	public static final String PURCHASE_ORDERS_FOR_SUPPLIER = "/purchase-orders/all";
	public static final String PURCHASE_ORDER = "purchase-orders/{purchaseOrderId}";
	public static final String DELETE_PURCHASE_ORDER = "purchase-orders/{purchaseOrderId}";

	public static final String SUBMIT_PURCHASE_ORDER = "/purchase-orders/workflow/{purchaseOrderId}/submit";

	public static final String APPROVE_PURCHASE_ORDER = "/purchase-orders/workflow/{purchaseOrderId}/authorise";
	public static final String RECEIVE_PURCHASE_ORDER = "/purchase-orders/{po_id}/stock-receipts";

}
