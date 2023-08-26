package com.qortex.inventory.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import com.qortex.inventory.common.constant.GlobalErrorConstant;
import com.qortex.inventory.common.constant.URLConstant;
import com.qortex.inventory.common.exception.SecurityApplicationException;
import com.qortex.inventory.common.exception.WareHouseDetailsNotFoundException;
import com.qortex.inventory.common.utils.PermissionCheck;
import com.qortex.inventory.common.utils.SetApiContext;
import com.qortex.inventory.common.utils.SetQSTSMModulesAndPermissions;
import com.qortex.inventory.dto.*;
import com.qortex.inventory.model.PurchaseOrder;
import com.qortex.inventory.model.Warehouse;
import com.qortex.inventory.service.InventoryWarehouseManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(URLConstant.BASE_URL)
public class InventoryWarehouseController {
    private static final String MODULE = "inventory_management";
    private static final String INITIATE = "initiate";
    private static final String AUTHORIZE = "authorize";
    private static final String VIEW = "view";
    private static final String SESSION_PAYLOAD = "sessionPayload";
    private static final String ADDING_INVENTORY_INFORMATION = " Adding the inventory information";
    private static final String MODULE_PERMISSION_INFO = "Checking the permission for the module";
    private static final String WAREHOUSE_ADD_START_IN_CONTROLLER_OF_NAME = "Warehouse add start in controller of name : {} ";
    private static final String WAREHOUSE_ADD_START_IN_CONTROLLER_OF_NAME_SUCCESS = "Warehouse add start in controller of name : {} success";
    private static final String WAREHOUSE_DELETE_START_IN_CONTROLLER = "Warehouse delete start in controller : ";
    private static final String WAREHOUSE_DELETE_START_IN_CONTROLLER_SUCCESS = "Warehouse delete start in controller : ";
    private static final String UPDATE_WAREHOUSE_STARTING_AT_CONTROLLER_OF_ID = "Update warehouse starting at controller of id : {}";
    private static final String UPDATE_USER_SUCCESS_OF_ID = "Update user success of id : {}";
    private static final String WAREHOUSE_GET_BY_ID_START_OF_ID = "Warehouse get by id start of id: {}";
    private static final String WAREHOUSE_GET_BY_ID_SUCCESS_OF_ID = "Warehouse get by id starting controller of id: {} success";
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InventoryWarehouseController.class);
    private final InventoryWarehouseManagementService warehouseService;
    private final PermissionCheck permissionCheck;

    @Autowired
    InventoryWarehouseController(InventoryWarehouseManagementService warehouseService, PermissionCheck permissionCheck) {
        this.warehouseService = warehouseService;
        this.permissionCheck = permissionCheck;
    }
    @Operation(summary = "This operation is responsible to add warehouses",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    @ApiResponses(
            value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
            })

    @PostMapping(URLConstant.ADD_WAREHOUSES)
    public ResponseEntity<ServiceApiResponseRecord> addInventoryWarehouse(
            @RequestBody @Valid WarehouseRequest addRequest,
            @RequestHeader("X-TENANT-ID") String tenantId,
            @RequestHeader("X-CHANNEL-ID") String channelId,
            @RequestHeader("X-CUSTOMER-ID") String customerId,
            @RequestHeader("AUTHORIZATION") String token,
            HttpServletRequest request) throws WareHouseDetailsNotFoundException {
        log.info(ADDING_INVENTORY_INFORMATION + MODULE);
        String moduleTitleSlug = "add_inventory_warehouse";
        List<Map<String, Set<String>>> modulesAndPermsListOne = SetQSTSMModulesAndPermissions.setModulesAndPermissions(INITIATE, moduleTitleSlug,true);
        List<Map<String, Set<String>>> modulesAndPermsListTwo = SetQSTSMModulesAndPermissions.setModulesAndPermissions(AUTHORIZE, moduleTitleSlug,true);
        log.info(MODULE_PERMISSION_INFO + INITIATE);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);
        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            log.info(WAREHOUSE_ADD_START_IN_CONTROLLER_OF_NAME, addRequest.warehouseName());
            if (permissionCheck.checkUpdate(sessionPayload.getModulesAndPermissions(), MODULE, modulesAndPermsListOne, modulesAndPermsListTwo)) {
                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                Warehouse inventoryResponse = warehouseService.addInventoryWarehouse(addRequest, sessionPayload.getUserId());
                ServiceApiResponseRecord warehouseResponse = new ServiceApiResponseRecord(serviceApiResponse, inventoryResponse);
                log.info(WAREHOUSE_ADD_START_IN_CONTROLLER_OF_NAME_SUCCESS, addRequest.warehouseName());
                return new ResponseEntity<>(warehouseResponse, HttpStatus.CREATED);
            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED, GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }
        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED, GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }
    }

    @Operation(summary = "This operation is responsible to update warehouses",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error."),})
    @PatchMapping("/warehouses/{warehouse-id}")
    public ResponseEntity<ServiceApiResponseRecord> updateWarehouse(@PathVariable(name = "warehouse-id", required = true) String id, @RequestBody @Valid WarehouseRequest updateRequest, @RequestHeader("X-TENANT-ID") String tenantId, @RequestHeader("X-CHANNEL-ID") String channelId, @RequestHeader("X-CUSTOMER-ID") String customerId, @RequestHeader("AUTHORIZATION") String token, HttpServletRequest request) throws WareHouseDetailsNotFoundException {
        log.info(UPDATE_WAREHOUSE_STARTING_AT_CONTROLLER_OF_ID, id);
        String moduleTitleSlug = "edit_inventory_warehouse_from_actions";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions.setModulesAndPermissionsSetOne(INITIATE, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);
        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(), MODULE, modulesAndPermsList)) {
                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                Warehouse warehouse = warehouseService.updateWareHouse(id, updateRequest, sessionPayload.getUserId());
                ServiceApiResponseRecord updateWareHouseResponse = new ServiceApiResponseRecord(serviceApiResponse, warehouse);
                log.info(UPDATE_USER_SUCCESS_OF_ID, warehouse.getId());
                return new ResponseEntity<>(updateWareHouseResponse, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED, GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED, GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }

    @Operation(summary = "This operation is responsible to get warehouses by id",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error."),})
    @GetMapping("/warehouses/{warehouse-id}")
    public ResponseEntity<ServiceApiResponseWarehouseRecord> getWarehouseById(
            @PathVariable(name = "warehouse-id") String id,
            @RequestParam("warehouseType") String warehouseType,
            @RequestHeader("X-TENANT-ID") String tenantId,
            @RequestHeader("X-CHANNEL-ID") String channelId,
            @RequestHeader("X-CUSTOMER-ID") String customerId,
            @RequestHeader("AUTHORIZATION") String token, HttpServletRequest request) {
        String moduleTitleSlug = "inventory_management_tab_inventory_warehouse";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions.setModulesAndPermissionsSetOne(VIEW, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);
        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(), MODULE, modulesAndPermsList)) {
                log.info(WAREHOUSE_GET_BY_ID_START_OF_ID, id);
                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                WarehouseResponse warehouse = warehouseService.getWarehouseById(warehouseType,id);
                ServiceApiResponseWarehouseRecord updateWareHouseResponse = new ServiceApiResponseWarehouseRecord(serviceApiResponse, warehouse);
                log.info(WAREHOUSE_GET_BY_ID_SUCCESS_OF_ID, warehouse.getWarehouseId());
                return new ResponseEntity<>(updateWareHouseResponse, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED, GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED, GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }
    }

    @Operation(summary = "This operation is responsible to deleting the warehouses",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error."),})
    @PutMapping(URLConstant.DELETE_WAREHOUSES)
    public ResponseEntity<ServiceApiResponseDelete> deleteWarehouse(
            @RequestBody DeleteWarehouse deleteList,
            @RequestHeader("X-TENANT-ID") String tenantId,
            @RequestHeader("X-CHANNEL-ID") String channelId,
            @RequestHeader("X-CUSTOMER-ID") String customerId,
            @RequestHeader("AUTHORIZATION") String token, HttpServletRequest request) {
        log.info(WAREHOUSE_DELETE_START_IN_CONTROLLER);

        String moduleTitleSlugOne = "delete_inventory_warehouse_from_view";
        String moduleTitleSlugTwo = "delete_inventory_warehouse_from_actions";

        List<Map<String, Set<String>>> modulesAndPermsListOne = SetQSTSMModulesAndPermissions.setModulesAndPermissions(INITIATE, moduleTitleSlugOne,true);
        List<Map<String, Set<String>>> modulesAndPermsListTwo = SetQSTSMModulesAndPermissions.setModulesAndPermissions(AUTHORIZE, moduleTitleSlugTwo,true);

        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);
        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.checkUpdate(sessionPayload.getModulesAndPermissions(), MODULE, modulesAndPermsListOne, modulesAndPermsListTwo)) {
                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                String description = warehouseService.deleteWarehouse(deleteList, sessionPayload.getUserId());
                ServiceApiResponseDelete warehouseResponse = new ServiceApiResponseDelete(serviceApiResponse, description);
                log.info(WAREHOUSE_DELETE_START_IN_CONTROLLER_SUCCESS);
                return new ResponseEntity<>(warehouseResponse, HttpStatus.OK);
            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED, GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }
        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED, GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }

    @Operation(summary = "This operation is responsible to get list of Active and Inactive warehouses",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @GetMapping("/inventories")
    public ResponseEntity<InventoryListResponse> getAllInventories(
            @RequestParam(value = "warehouseName", required = false) String warehouseName,
            @RequestParam(value = "warehouseType", required = false) String warehouseType,
            @RequestParam(value = "warehouseCode", required = false) String warehouseCode,
            @RequestParam("offset") Integer offset,
            @RequestParam("pageSize") Integer pageSize,
            @RequestHeader("X-TENANT-ID") String tenantId,
            @RequestHeader("X-CHANNEL-ID") String channelId,
            @RequestHeader("X-CUSTOMER-ID") String customerId,
            @RequestHeader("AUTHORIZATION") String token,
            HttpServletRequest request
    ){
        log.info("Warehouse list fetch start in controller");
        String moduleTitleSlug = "inventory_management_tab_inventory_warehouse_list";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(VIEW, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);

        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList)) {
                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                Page<WarehouseResponse> warehouse = warehouseService.getAllInventories(warehouseName,warehouseType,
                        warehouseCode,offset, pageSize);
                InventoryListResponse inventoryList = new InventoryListResponse(serviceApiResponse,
                        warehouse);
                return new ResponseEntity<>(inventoryList, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }

    @Operation(summary = "This operation is responsible to create purchase order",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @PostMapping(URLConstant.PURCHASE_ORDERS)
    public ResponseEntity<PurchaseOrderResponseRecord> createPurchaseOrder(
                                                @RequestBody PurchaseOrderRecord purchaseOrderRecord,
                                                @RequestHeader("X-TENANT-ID") String tenantId,
                                                @RequestHeader("X-CHANNEL-ID") String channelId,
                                                @RequestHeader("X-CUSTOMER-ID") String customerId,
                                                @RequestHeader("AUTHORIZATION") String token,
                                                HttpServletRequest request){
        String moduleTitleSlug = "create_purchase_order";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(INITIATE, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);

        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList)) {
                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                String purchaseOrder = warehouseService.createPurchaseOrder(purchaseOrderRecord, sessionPayload.getUserId());
               PurchaseOrderResponseRecord purchaseOrderResponseRecord = new PurchaseOrderResponseRecord(serviceApiResponse, purchaseOrder);
                return new ResponseEntity<>(purchaseOrderResponseRecord, HttpStatus.CREATED);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }
    }
    @Operation(summary = "This operation is responsible to edit purchase order",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @PatchMapping(URLConstant.PURCHASE_ORDER)
    public ResponseEntity<PurchaseOrderResponseRecord> updatePurchaseOrder(
                                                                           @PathVariable(name = "purchaseOrderId", required = true) String purchaseOrderId,
                                                                           @RequestBody UpdatePurchaseOrderRecord purchaseOrderRecord,
                                                                           @RequestHeader("X-TENANT-ID") String tenantId,
                                                                           @RequestHeader("X-CHANNEL-ID") String channelId,
                                                                           @RequestHeader("X-CUSTOMER-ID") String customerId,
                                                                           @RequestHeader("AUTHORIZATION") String token,
                                                                           HttpServletRequest request){
        String moduleTitleSlug = "edit_purchase_order";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(INITIATE, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);

        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList)) {
                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                String purchaseOrder = warehouseService.updatePurchaseOrder(purchaseOrderRecord, sessionPayload.getUserId(), purchaseOrderId);
                PurchaseOrderResponseRecord purchaseOrderResponseRecord = new PurchaseOrderResponseRecord(serviceApiResponse, purchaseOrder);
                return new ResponseEntity<>(purchaseOrderResponseRecord, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }

    @Operation(summary = "This operation is responsible to get all purchase orders",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @GetMapping(URLConstant.PURCHASE_ORDERS)
    public ResponseEntity<PurchaseOrderListResponseRecord> getAllPurchaseOrders(
            @RequestParam(name = "statusCode", required = false) String statusCode,
            @RequestParam(name = "issueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam(name = "supplierName", required = false) String supplierName,
            @RequestParam(name = "poNumber", required = false) String poNumber,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "offSet") Integer offSet,
            @RequestHeader("X-TENANT-ID") String tenantId,
            @RequestHeader("X-CHANNEL-ID") String channelId,
            @RequestHeader("X-CUSTOMER-ID") String customerId,
            @RequestHeader("AUTHORIZATION") String token,
            HttpServletRequest request){
        String moduleTitleSlugAll = "list_all_purchase_order";
        String moduleTitleSlugMy = "list_my_purchase_order";
        String moduleTitleSlugApprover = "approve_purchase_order";

        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(VIEW,AUTHORIZE, moduleTitleSlugAll,moduleTitleSlugMy,moduleTitleSlugApprover);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);

        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            PurchaseOrderListSegregator segregator =  permissionCheck.checkListSegregate(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList);
            boolean allFields = segregator.isAllList() || segregator.isMyList() || segregator.isApprover();
            if (segregator.isValid() && allFields) {

                PurchaseOrderSearch poSearchAttributes = poSearchAttributes(statusCode,issueDate, supplierName, poNumber);
                PurchaseOrderPage purchaseOrderPage = purchaseOrderPage(pageSize,offSet);

                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                Page<PurchaseOrder> purchaseOrders = warehouseService.getPurchaseOrders(poSearchAttributes, purchaseOrderPage,segregator,sessionPayload.getUserId());
                PurchaseOrderListResponseRecord purchaseOrderResponseRecord = new
                        PurchaseOrderListResponseRecord(serviceApiResponse, purchaseOrders);
                return new ResponseEntity<>(purchaseOrderResponseRecord, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }

    @Operation(summary = "This operation is responsible to get purchase order by id",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @GetMapping(URLConstant.PURCHASE_ORDER)
    public ResponseEntity<PurchaseRecord> getPurchaseOrderById(
            @PathVariable(name = "purchaseOrderId") String purchaseOrderId,
            @RequestHeader("X-TENANT-ID") String tenantId,
            @RequestHeader("X-CHANNEL-ID") String channelId,
            @RequestHeader("X-CUSTOMER-ID") String customerId,
            @RequestHeader("AUTHORIZATION") String token,
            HttpServletRequest request
    ){
        String moduleTitleSlugAll = "list_all_purchase_order";
        String moduleTitleSlugMy = "list_my_purchase_order";
        String moduleTitleSlugApprover = "approve_purchase_order";

        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(VIEW,AUTHORIZE, moduleTitleSlugAll,moduleTitleSlugMy,moduleTitleSlugApprover);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);

        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            PurchaseOrderListSegregator segregator =  permissionCheck.checkListSegregate(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList);
            boolean allFields = segregator.isAllList() || segregator.isMyList() || segregator.isApprover();
            if (segregator.isValid() && allFields) {
                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                PurchaseOrder purchaseOrders = warehouseService.getPurchaseOrder(purchaseOrderId, segregator,sessionPayload.getUserId());
                PurchaseRecord purchaseOrderResponseRecord = new
                        PurchaseRecord(serviceApiResponse, purchaseOrders);
                return new ResponseEntity<>(purchaseOrderResponseRecord, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }
    }

    @Operation(summary = "This operation is responsible to delete a purchase orders",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @DeleteMapping(URLConstant.DELETE_PURCHASE_ORDER)
    public ResponseEntity<PurchaseOrderResponseRecord> deletePurchaseOrders(
            @PathVariable(name = "purchaseOrderId") String purchaseOrderId,
            @RequestHeader("X-TENANT-ID") String tenantId,
            @RequestHeader("X-CHANNEL-ID") String channelId,
            @RequestHeader("X-CUSTOMER-ID") String customerId,
            @RequestHeader("AUTHORIZATION") String token,
            HttpServletRequest request){
        String moduleTitleSlug = "delete_purchase_order";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(INITIATE, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);
        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList)) {

                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                String deletedPurchaseOrder = warehouseService.deletePurchaseOrder(purchaseOrderId, sessionPayload.getUserId());
                PurchaseOrderResponseRecord purchaseOrderResponseRecord = new
                        PurchaseOrderResponseRecord(serviceApiResponse, deletedPurchaseOrder);
                return new ResponseEntity<>(purchaseOrderResponseRecord, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }

    @Operation(summary = "This operation is responsible to submit a purchase order",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @PutMapping(URLConstant.SUBMIT_PURCHASE_ORDER)
    public ResponseEntity<SubmitPurchaseOrderResponseRecord> submitPurchaseOrder(@PathVariable(name = "purchaseOrderId") String purchaseOrderId,
                                                                           @RequestHeader("X-TENANT-ID") String tenantId,
                                                                           @RequestHeader("X-CHANNEL-ID") String channelId,
                                                                           @RequestHeader("X-CUSTOMER-ID") String customerId,
                                                                           @RequestHeader("AUTHORIZATION") String token,
                                                                           HttpServletRequest request){

        String moduleTitleSlug = "submit_purchase_order";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(INITIATE, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);
        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList)) {

                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                PurchaseOrderStatusResponse purchaseOrderStatusResponse = warehouseService.submitPurchaseOrder(purchaseOrderId, sessionPayload.getUserId());
                SubmitPurchaseOrderResponseRecord purchaseOrderResponseRecord = new
                        SubmitPurchaseOrderResponseRecord(serviceApiResponse, purchaseOrderStatusResponse);
                return new ResponseEntity<>(purchaseOrderResponseRecord, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }


    @Operation(summary = "This operation is responsible to approve a purchase order",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @PutMapping(URLConstant.APPROVE_PURCHASE_ORDER)
    public ResponseEntity<SubmitPurchaseOrderResponseRecord> approvePurchaseOrder(@PathVariable(name = "purchaseOrderId") String purchaseOrderId,
                                                                                 @RequestBody ApproveOrderRequest approveOrderRequest,
                                                                                 @RequestHeader("X-TENANT-ID") String tenantId,
                                                                                 @RequestHeader("X-CHANNEL-ID") String channelId,
                                                                                 @RequestHeader("X-CUSTOMER-ID") String customerId,
                                                                                 @RequestHeader("AUTHORIZATION") String token,
                                                                                 HttpServletRequest request){

        String moduleTitleSlug = "approve_purchase_order";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(INITIATE, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);
        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList)) {

                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                PurchaseOrderStatusResponse purchaseOrderStatusResponse = warehouseService.approvePurchaseOrder(purchaseOrderId, sessionPayload.getUserId(), approveOrderRequest);
                SubmitPurchaseOrderResponseRecord purchaseOrderResponseRecord = new
                        SubmitPurchaseOrderResponseRecord(serviceApiResponse, purchaseOrderStatusResponse);
                return new ResponseEntity<>(purchaseOrderResponseRecord, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }

    @Operation(summary = "This operation is responsible to get all purchase orders by supplier id",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @GetMapping(URLConstant.PURCHASE_ORDERS_FOR_SUPPLIER)
    public ResponseEntity<PurchaseOrderListResponseRecord> getAllPurchaseOrdersBySupplierId(
            @RequestParam(name = "supplier_id", required = true) String supplier_id,
            @RequestParam(name = "statusCode", required = false) String statusCode,
            @RequestParam(name = "issueDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam(name = "supplierName", required = false) String supplierName,
            @RequestParam(name = "poNumber", required = false) String poNumber,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "offSet") Integer offSet,
            @RequestHeader("X-TENANT-ID") String tenantId,
            @RequestHeader("X-CHANNEL-ID") String channelId,
            @RequestHeader("X-CUSTOMER-ID") String customerId,
            @RequestHeader("AUTHORIZATION") String token,
            HttpServletRequest request){

        String moduleTitleSlug = "supplier_list_all";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(VIEW, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);

        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList)) {

                PurchaseOrderSearch poSearchAttributes = poSearchAttributes(statusCode,issueDate, supplierName, poNumber);
                PurchaseOrderPage purchaseOrderPage = purchaseOrderPage(pageSize,offSet);

                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                Page<PurchaseOrder> purchaseOrders = warehouseService.getPurchaseOrdersBySupplierId(poSearchAttributes, purchaseOrderPage,supplier_id);
                PurchaseOrderListResponseRecord purchaseOrderResponseRecord = new
                        PurchaseOrderListResponseRecord(serviceApiResponse, purchaseOrders);
                return new ResponseEntity<>(purchaseOrderResponseRecord, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }

    @Operation(summary = "This operation is responsible to receive a purchase order",
            security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Bad request."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access."),
                    @ApiResponse(responseCode = "403", description = "Forbidden."),
                    @ApiResponse(responseCode = "404", description = "Not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error."),
            })
    @PostMapping(URLConstant.RECEIVE_PURCHASE_ORDER)
    public ResponseEntity<PurchaseOrderReceiveResponseWithContext> receivePurchaseOrder(@PathVariable(name = "po_id") String purchaseOrderId,
                                                                             @RequestBody PurchaseOrderReceiveRequest purchaseOrderReceiveRequest,
                                                                             @RequestHeader("X-TENANT-ID") String tenantId,
                                                                             @RequestHeader("X-CHANNEL-ID") String channelId,
                                                                             @RequestHeader("X-CUSTOMER-ID") String customerId,
                                                                             @RequestHeader("AUTHORIZATION") String token,
                                                                             HttpServletRequest request){
        //String moduleTitleSlug = "receive_purchase_order";
        String moduleTitleSlug = "submit_purchase_order";
        List<Map<String, Set<String>>> modulesAndPermsList = SetQSTSMModulesAndPermissions
                .setModulesAndPermissionsSetOne(INITIATE, moduleTitleSlug);
        UserSessionWithModulesAndPermissions sessionPayload = getSessionPayload(request);
        if (sessionPayload != null && sessionPayload.getModulesAndPermissions() != null) {
            if (permissionCheck.check(sessionPayload.getModulesAndPermissions(),
                    MODULE, modulesAndPermsList)) {
                ServiceApiResponse serviceApiResponse = SetApiContext.setApiContext(tenantId, channelId, customerId);
                PurchaseOrderReceiveResponse purchaseOrders = warehouseService.receivePurchaseOrder(purchaseOrderId, purchaseOrderReceiveRequest, sessionPayload.getUserId());
                PurchaseOrderReceiveResponseWithContext purchaseOrderReceiveResponseWithContext =  new PurchaseOrderReceiveResponseWithContext(serviceApiResponse, purchaseOrders);
                return new ResponseEntity<>(purchaseOrderReceiveResponseWithContext, HttpStatus.OK);

            } else {
                log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
                throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                        GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            }

        } else {
            log.error(GlobalErrorConstant.MODULE_PERMISSION_ERROR);
            throw new SecurityApplicationException(HttpStatus.UNAUTHORIZED,
                    GlobalErrorConstant.MODULE_PERMISSION_ERROR);
        }

    }


    private PurchaseOrderSearch poSearchAttributes(String statusCode, LocalDate issueDate,
                                                   String supplierName,String poNumber){
        return new PurchaseOrderSearch(statusCode,issueDate,supplierName, poNumber);

    }

    private PurchaseOrderPage purchaseOrderPage(Integer pageSize, Integer offSet){
       PurchaseOrderPage orderPage =  new PurchaseOrderPage();
       orderPage.setPageSize(pageSize);
       orderPage.setPageNumber(offSet);
       return orderPage;
    }

    private UserSessionWithModulesAndPermissions getSessionPayload(HttpServletRequest request) {
        return (UserSessionWithModulesAndPermissions) request.getAttribute(SESSION_PAYLOAD);
    }
}