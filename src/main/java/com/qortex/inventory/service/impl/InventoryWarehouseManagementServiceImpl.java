package com.qortex.inventory.service.impl;

import com.qortex.inventory.common.constant.GlobalErrorConstant;
import com.qortex.inventory.common.enums.AssigneeType;
import com.qortex.inventory.common.enums.PurchaseOrderStatus;
import com.qortex.inventory.common.enums.WarehouseStatus;
import com.qortex.inventory.common.exception.*;
import com.qortex.inventory.controller.InventoryWarehouseController;
import com.qortex.inventory.dto.*;
import com.qortex.inventory.model.*;
import com.qortex.inventory.repository.*;
import com.qortex.inventory.service.InventoryWarehouseManagementService;
import jakarta.transaction.Transactional;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Service
@Transactional
@SuppressWarnings("java:S2245")
public class InventoryWarehouseManagementServiceImpl implements InventoryWarehouseManagementService {
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    private static final String WAREHOUSE_ADD_SUCCESS = "Warehouse add start in service of name : {} success";
    private static final String DELETE_WAREHOUSE = "Delete warehouse starting at service :";
    private static final String CHECK_DELETE_IDS = "Checking the deletion ids from list : {}";
    private static final String WAREHOUSE_DELETE_UNSUCCESSFUL = "Warehouse not deleting successfully please try again  :";
    private static final String WAREHOUSE_IDS_NOT_PRESENT = "Warehouse ids not present in list  :";
    private static final String UPDATE_WAREHOUSE = "Update warehouse of id : {}";
    private static final String WAREHOUSE_UPDATES_SUCCESS = "Warehouse updates successful in service of id : {}";
    private static final String WAREHOUSE_GET_BY_ID_START_SERVICE_OF_ID = "Warehouse get by id start service of id {} ";
    private static final String WAREHOUSE_NOT_FOUND_FOR_THIS_ID = "Warehouse not found for this id : ";

    private static final String SALESMAN="SALESMAN";
    private static final String ENTERPRISE ="ENTERPRISE";
    private static final String DIGITAL ="DIGITAL";
    private static final String PHYSICAL ="PHYSICAL";
    private static final String WAREHOUSE_DELETE_SUCCESSFUL = "Delete successfully.";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InventoryWarehouseController.class);
    private final InventoryWarehouseRepository inventoryWarehouseRepository;
    private final InventoryWarehouseAddressRepository inventoryWarehouseAddressRepository;
    private final InventoryListRepository inventoryListRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderProductRepository purchaseOrderProductRepository;

    private final SupplierRepository supplierRepository;

    private final PurchaseOrderListRepository purchaseOrderListRepository;

    private final MyPurchaseOrderListRepository myPurchaseOrderListRepository;

    private final ApprovalPurchaseOrderListRepository approvalPurchaseOrderListRepository;

    private final PurchaseOrderSupplierRepository purchaseOrderSupplierRepository;

    private final ProductRepository productRepository;

    private final StockReceiptRepository stockReceiptRepository;

    public InventoryWarehouseManagementServiceImpl(InventoryWarehouseRepository inventoryWarehouseRepository,
                                                   InventoryWarehouseAddressRepository inventoryWarehouseAddressRepository,
                                                   InventoryListRepository inventoryListRepository,
                                                   PurchaseOrderRepository purchaseOrderRepository,
                                                   PurchaseOrderProductRepository purchaseOrderProductRepository,
                                                   SupplierRepository supplierRepository,
                                                   PurchaseOrderListRepository purchaseOrderListRepository,
                                                   MyPurchaseOrderListRepository myPurchaseOrderListRepository,
                                                   ApprovalPurchaseOrderListRepository approvalPurchaseOrderListRepository,
                                                   PurchaseOrderSupplierRepository purchaseOrderSupplierRepository,
                                                   ProductRepository productRepository,
                                                   StockReceiptRepository stockReceiptRepository) {
        this.inventoryWarehouseRepository = inventoryWarehouseRepository;
        this.inventoryWarehouseAddressRepository = inventoryWarehouseAddressRepository;
        this.inventoryListRepository = inventoryListRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderProductRepository = purchaseOrderProductRepository;
        this.supplierRepository = supplierRepository;
        this.purchaseOrderListRepository = purchaseOrderListRepository;
        this.myPurchaseOrderListRepository = myPurchaseOrderListRepository;
        this.approvalPurchaseOrderListRepository = approvalPurchaseOrderListRepository;
        this.purchaseOrderSupplierRepository = purchaseOrderSupplierRepository;
        this.productRepository = productRepository;
        this.stockReceiptRepository = stockReceiptRepository;
    }

    public Warehouse addInventoryWarehouse(WarehouseRequest warehouseRequest, String userId)
            throws WareHouseDetailsNotFoundException {

        inventoryWarehouseRepository.findByWarehouseNameAndStatusCodeNot(warehouseRequest.warehouseName(),
                        WarehouseStatus.DELETED.name())
                .ifPresent(inventory -> {
                    log.error("Inventory with warehouse name {} already exists.", inventory.getWarehouseName());
                    throw new InventoryNameAlreadyExist(HttpStatus.CONFLICT,
                            "Warehouse name already exist");
                });
        log.info(WAREHOUSE_ADD_SUCCESS, warehouseRequest.warehouseName());
         return inventoryWarehouseRepository.save(this.getWarehouse(warehouseRequest, userId));

    }

    private Warehouse getWarehouse(WarehouseRequest warehouseRequest, String userId) {

        Warehouse inventory = new Warehouse();
        WarehouseAddress address = new WarehouseAddress();
        inventory.setWarehouseName(warehouseRequest.warehouseName());
        inventory.setWarehouseType(warehouseRequest.warehouseType());
        inventory.setWarehouseCode(warehouseRequest.warehouseCode());
        inventory.setAssignedId(warehouseRequest.assignedId());
        inventory.setStatusCode(warehouseRequest.warehouseStatusCode());
        inventory.setCreatedBy(userId);
        inventory.setCreatedOn(LocalDateTime.now());

        address.setDoorName(warehouseRequest.doorName());
        address.setBuildingName(warehouseRequest.buildingName());
        address.setStreet(warehouseRequest.street());
        address.setCity(warehouseRequest.city());
        address.setState(warehouseRequest.state());
        address.setCountry(warehouseRequest.country());
        address.setZipCode(warehouseRequest.zipCode());
        address.setStatusCode(warehouseRequest.warehouseAddressStatusCode());
        address.setCreatedBy(userId);
        address.setCreatedOn(LocalDateTime.now());

        inventory.setAddress(address);
        address.setWarehouse(inventory);
        return inventory;
    }


    public String deleteWarehouse(DeleteWarehouse deleteList, String userId) {
        log.info(DELETE_WAREHOUSE);
        int batchSize = 100;
        List<List<String>> warehouseIdBatches = ListUtils.partition(deleteList.getWarehouse_id(), batchSize);
        int totalDeletedWarehouses = 0;
        int totalDeletedAddresses = 0;

        if (!deleteList.getWarehouse_id().isEmpty()) {
            log.info(CHECK_DELETE_IDS, deleteList.getWarehouse_id());
            for (List<String> warehouseIdBatch : warehouseIdBatches) {
                int deletedWarehouses = inventoryWarehouseRepository.deleteWarehouse(WarehouseStatus.DELETED.name(),
                        LocalDateTime.now(), userId, warehouseIdBatch);

                int deletedAddresses = inventoryWarehouseAddressRepository.deleteWarehouseAddress(
                        WarehouseStatus.DELETED.name(), LocalDateTime.now(), userId, warehouseIdBatch);

                totalDeletedWarehouses += deletedWarehouses;
                totalDeletedAddresses += deletedAddresses;
            }

            if (totalDeletedWarehouses > 0 && totalDeletedAddresses > 0) {
                log.info(WAREHOUSE_DELETE_SUCCESSFUL);
                return "Delete successfully.";
            } else {
                log.error(WAREHOUSE_DELETE_UNSUCCESSFUL);
                throw new WarehouseDeletionException(HttpStatus.BAD_REQUEST,
                        GlobalErrorConstant.INVENTORY_NOT_DELETE_CORRECLTY);
            }
        } else {
            log.error(WAREHOUSE_IDS_NOT_PRESENT);
            throw new NotFoundException(HttpStatus.NOT_FOUND, GlobalErrorConstant.INVENTORY_ID_NOT_FOUND);
        }
    }

    @Override
    public Warehouse updateWareHouse(String id, WarehouseRequest updateRequest, String userId)
            throws WareHouseDetailsNotFoundException {
        log.info(UPDATE_WAREHOUSE, id);
        Warehouse warehouse = inventoryWarehouseRepository.findByIdAndStatusCodeNot(id, WarehouseStatus.DELETED.name())
                .orElseThrow(() -> new WareHouseDetailsNotFoundException(HttpStatus.NOT_FOUND,
                        WAREHOUSE_NOT_FOUND_FOR_THIS_ID + id));

        // updating warehouse
        warehouse.setWarehouseName(updateRequest.warehouseName());
        warehouse.setWarehouseCode(updateRequest.warehouseCode());
        warehouse.setWarehouseType(updateRequest.warehouseType());
        warehouse.setAssignedId(updateRequest.assignedId());
        warehouse.setStatusCode(updateRequest.warehouseStatusCode());
        warehouse.setUpdatedBy(userId);
        warehouse.setUpdatedOn(LocalDateTime.now());

        // Updating address
        WarehouseAddress address = warehouse.getAddress();
        address.setBuildingName(updateRequest.buildingName());
        address.setStreet(updateRequest.street());
        address.setCity(updateRequest.city());
        address.setState(updateRequest.state());
        address.setDoorName(updateRequest.doorName());
        address.setCountry(updateRequest.country());
        address.setState(updateRequest.state());
        address.setZipCode(updateRequest.zipCode());
        address.setUpdatedBy(userId);
        address.setUpdatedOn(LocalDateTime.now());
        // setting address into warehouse
        log.info(WAREHOUSE_UPDATES_SUCCESS, warehouse.getId());
        return inventoryWarehouseRepository.save(warehouse);
    }

    @Override
    @SuppressWarnings("java:S2201")
    public WarehouseResponse getWarehouseById(String warehouseType, String id) {
        log.info(WAREHOUSE_GET_BY_ID_START_SERVICE_OF_ID, id);
        return inventoryWarehouseRepository.findByIdAndStatusCodeNot(id, WarehouseStatus.DELETED.name())
                .map( warehouse->{
                    WarehouseResponse response = new WarehouseResponse();

                    if(warehouse.getAssignedId() == null || warehouse.getAssignedId().isEmpty()){
                       throw  new WareHouseDetailsNotFoundException(HttpStatus.NOT_FOUND, "Assign Id is not present for this warehouse");
                    }

                    //Set warehouse data to the response
                    response.setWarehouseId(warehouse.getId());
                    response.setWarehouseName(warehouse.getWarehouseName());
                    response.setWarehouseCode(warehouse.getWarehouseCode());
                    response.setWarehouseType(warehouse.getWarehouseType());
                    response.setAssignedId(warehouse.getAssignedId());
                    response.setWarehouseStatusCode(warehouse.getStatusCode());
                    response.setWarehouseCreatedBy(warehouse.getCreatedBy());
                    response.setWarehouseCreatedOn(warehouse.getCreatedOn());
                    response.setWarehouseUpdatedBy(warehouse.getUpdatedBy());
                    response.setWarehouseUpdatedOn(warehouse.getUpdatedOn());
                    //Set Address data to the response
                    Optional.ofNullable(warehouse.getAddress())
                                    .map(address -> {
                                        response.setWarehouseAddressId(address.getId());
                                        response.setDoorNo(address.getDoorName());
                                        response.setBuildingName(address.getBuildingName());
                                        response.setStreet(address.getStreet());
                                        response.setCity(address.getCity());
                                        response.setState(address.getState());
                                        response.setCountry(address.getCountry());
                                        response.setZip_code(address.getZipCode());
                                        response.setAddressStatusCode(address.getStatusCode());
                                        response.setWarehouseAddressCreatedOn(address.getCreatedOn());
                                        response.setWarehouseAddressCreatedBy(address.getCreatedBy());
                                        response.setWarehouseAddressUpdatedOn(address.getUpdatedOn());
                                        response.setWarehouseAddressUpdatedBy(address.getUpdatedBy());
                                        return response;
                                    }).orElseThrow(()-> {
                                        String message = "Warehouse address not found for this warehouse id: " + id;
                                        WareHouseDetailsNotFoundException exception = new WareHouseDetailsNotFoundException(HttpStatus.NOT_FOUND, message);
                                        log.error(message, exception);
                                        throw exception;
                                      }
                                   );
                    switch (warehouseType) {
                        case SALESMAN -> response.setAssignUserName(inventoryWarehouseRepository.getUserAssignName(warehouse.getAssignedId()));
                        case ENTERPRISE -> response.setAssignUserName(inventoryWarehouseRepository.getUserAssignEnterpriseName(warehouse.getAssignedId()));
                        case DIGITAL, PHYSICAL -> response.setAssignUserName(warehouse.getWarehouseName());
                    }

                    return response;
                }).orElseThrow(()-> new WareHouseDetailsNotFoundException(HttpStatus.NOT_FOUND,
                        "Warehouse not found for this id :" + id));
    }

    @Override
    public Page<WarehouseResponse> getAllInventories(String warehouseName, String warehouseType, String warehouseCode,
                                             Integer offset, Integer pageSize) {
        List<WarehouseResponse> warehouseList = new ArrayList<>();
        Pageable pageable = PageRequest.of(offset, pageSize, Sort.by(Sort.Direction.ASC, "warehouseName"));
        log.info("Get all inventories service start ");
        InventorySearchCriteria inventorySearchCriteria = new InventorySearchCriteria(warehouseName,warehouseType,warehouseCode);
        // set offset, pageSize
        InventoryPage inventoryPage = new InventoryPage();
        inventoryPage.setPageSize(pageSize);
        inventoryPage.setPageNumber(offset);
        Page<Warehouse> warehouses = inventoryListRepository.findAllWarehouses(inventoryPage, inventorySearchCriteria);

        if(warehouses.isEmpty()){
            return new PageImpl<>(warehouseList, pageable, 0);
        }

        warehouseList = warehouses.stream().map(warehouse -> {
            WarehouseResponse warehouseResponse =  new WarehouseResponse();
            if(AssigneeType.SALESMAN.name().equalsIgnoreCase(warehouse.getWarehouseType()) && (warehouse.getAssignedId() != null && !warehouse.getAssignedId().isEmpty())){
                warehouseResponse.setAssignUserName(inventoryWarehouseRepository.getUserAssignName(warehouse.getAssignedId()));
            } else if (AssigneeType.ENTERPRISE.name().equalsIgnoreCase(warehouse.getWarehouseType()) && (warehouse.getAssignedId() != null && !warehouse.getAssignedId().isEmpty())){
                warehouseResponse.setAssignUserName(inventoryWarehouseRepository.getUserAssignEnterpriseName(warehouse.getAssignedId()));
            } else if (AssigneeType.DIGITAL.name().equalsIgnoreCase(warehouse.getWarehouseType()) ||
                    AssigneeType.PHYSICAL.name().equalsIgnoreCase(warehouse.getWarehouseType())
            ){
                warehouseResponse.setAssignUserName(warehouse.getWarehouseName());
            }
            warehouseResponse.setWarehouseId(warehouse.getId());
            warehouseResponse.setWarehouseStatusCode(warehouse.getStatusCode());
            warehouseResponse.setWarehouseType(warehouse.getWarehouseType());
            warehouseResponse.setAssignedId(warehouse.getAssignedId());
            warehouseResponse.setWarehouseName(warehouse.getWarehouseName());
            warehouseResponse.setWarehouseCode(warehouse.getWarehouseCode());
            return warehouseResponse;
        }).collect(Collectors.toList());
        return new PageImpl<>(warehouseList, pageable, warehouses.getTotalElements());
    }

    @Override
    public String createPurchaseOrder(PurchaseOrderRecord purchaseOrderRecord, String userId) {
        log.info("Creating purchase order {} ", purchaseOrderRecord);
        return Optional.ofNullable(purchaseOrderRecord)
                .map(record -> setPurchaseOrder(record, userId))
                .map(item -> purchaseOrderRepository.save(item).getId())
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Cannot create purchase order"));

    }

    private PurchaseOrder setPurchaseOrder(PurchaseOrderRecord purchaseOrderRecord, String userId) {
        PurchaseOrder purchaseOrder = settingPurchaseOrderFields(purchaseOrderRecord, userId);
        List<PurchaseOrderProductItem> purchaseOrderProductItems = Optional.ofNullable(purchaseOrderRecord.purchaseOrderProductItemsList())
                .map(item ->
                        item
                                .stream()
                                .map(record -> mapToProductItem(record, userId))
                                .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Products cannot be empty for creating purchase order"));

        // Set the PurchaseOrder reference in each PurchaseOrderProductItem
        for (PurchaseOrderProductItem productItem : purchaseOrderProductItems) {
            productItem.setPurchaseOrder(purchaseOrder);
        }

        purchaseOrder.setPurchaseOrderProductItems(purchaseOrderProductItems);
        return purchaseOrder;
    }

    private PurchaseOrder settingPurchaseOrderFields(PurchaseOrderRecord purchaseOrderRecord, String userId){
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setEntityId(purchaseOrderRecord.entityId());

        // Create PO number
        if(PurchaseOrderStatus.DRAFT.name().equalsIgnoreCase(purchaseOrderRecord.statusCode())){
            purchaseOrder.setPoNumber("Draft " + generateRandomString(10));
        }else {
            purchaseOrder.setPoNumber(generateRandomString(10));
        }
        //get supplier by id
        Supplier supplier = supplierRepository.findById(purchaseOrderRecord.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Supplier not exist"));

        purchaseOrder.setSupplierId(supplier);
        purchaseOrder.setPoIssueDate(purchaseOrderRecord.poIssueDate());
        purchaseOrder.setPoReceivedDate(purchaseOrderRecord.poReceivedDate());
        purchaseOrder.setUserId(userId);
        purchaseOrder.setWarehouseId(purchaseOrderRecord.warehouseId());
        purchaseOrder.setSubtotalAmount(purchaseOrderRecord.subtotalAmount());
        purchaseOrder.setShippingAmount(purchaseOrderRecord.shippingAmount());
        purchaseOrder.setTax(purchaseOrderRecord.tax());
        purchaseOrder.setTotalAmount(purchaseOrderRecord.totalAmount());
        purchaseOrder.setNote(purchaseOrderRecord.note());
        purchaseOrder.setStatusCode(purchaseOrderRecord.statusCode());
        purchaseOrder.setCreatedBy(userId);
        purchaseOrder.setCreatedOn(LocalDateTime.now());
        purchaseOrder.setQuotationNumber(purchaseOrderRecord.quotationNumber());
        return purchaseOrder;
    }

    private PurchaseOrderProductItem mapToProductItem(PurchaseOrderProductItemRecord record, String userId) {
        PurchaseOrderProductItem productItem = new PurchaseOrderProductItem();
        productItem.setProductName(record.productName());
        productItem.setProductDescription(record.productDescription());
        productItem.setUnitPrice(record.unitPrice());
        productItem.setQuantity(record.quantity());
        productItem.setTax(record.tax());
        productItem.setProdId(record.prodId());
        productItem.setDiscount(record.discount());
        productItem.setRate(record.rate());
        productItem.setTotalPrice(record.totalPrice());
        productItem.setStatusCode(record.statusCode());
        productItem.setCreatedOn(LocalDateTime.now());
        productItem.setCreatedBy(userId);
        return productItem;
    }

    @Override
    public String updatePurchaseOrder(UpdatePurchaseOrderRecord purchaseOrderRecord, String userId, String purchaseOrderId) {
        log.info("Update start for purchase order id {}", purchaseOrderId);
        return purchaseOrderRepository.findByIdAndStatusCode(purchaseOrderId,
                        PurchaseOrderStatus.SAVED.name())
                .map(purchaseOrder -> {
                    PurchaseOrder updatedPurchaseOrder = setPurchaseOrderDetailsForUpdate(purchaseOrder, purchaseOrderRecord, userId);
                    //update or add products
                    if (!purchaseOrderRecord.purchaseOrderProductItemsList().isEmpty()) {
                        List<PurchaseOrderProductItem> updatedProductItems = purchaseOrderRecord.purchaseOrderProductItemsList()
                                .stream()
                                .map(product -> purchaseOrderProductRepository
                                        .findByIdAndStatusCodeNot(product.prodId(), PurchaseOrderStatus.DELETED.name())
                                        .map(existingProduct -> setExistingUpdatePo(existingProduct, product, updatedPurchaseOrder, userId))
                                        .orElseGet(() -> setNewProductInUpdatePo(product, updatedPurchaseOrder, userId)))
                                .collect(Collectors.toList());
                        updatedPurchaseOrder.setPurchaseOrderProductItems(updatedProductItems);
                    }
                    return purchaseOrderRepository.save(updatedPurchaseOrder).getId();
                })
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Cannot find purchase order to update"));
    }

    private PurchaseOrderProductItem setExistingUpdatePo(PurchaseOrderProductItem existingProduct,
                                                            PurchaseOrderProductItemRecord product,
                                                            PurchaseOrder updatedPurchaseOrder, String userId) {
        existingProduct.setProdId(product.prodId());
        existingProduct.setUnitPrice(product.unitPrice());
        existingProduct.setQuantity(product.quantity());
        existingProduct.setTax(product.tax());
        existingProduct.setDiscount(product.discount());
        existingProduct.setTotalPrice(product.totalPrice());
        existingProduct.setStatusCode(product.statusCode());
        existingProduct.setUpdatedOn(LocalDateTime.now());
        existingProduct.setUpdatedBy(userId);
        existingProduct.setPurchaseOrder(updatedPurchaseOrder);
        return existingProduct;
    }

    private PurchaseOrderProductItem setNewProductInUpdatePo(PurchaseOrderProductItemRecord product,
                                                             PurchaseOrder updatedPurchaseOrder, String userId) {
        PurchaseOrderProductItem productItem = new PurchaseOrderProductItem();
        productItem.setUnitPrice(product.unitPrice());
        productItem.setQuantity(product.quantity());
        productItem.setProdId(product.prodId());
        productItem.setTax(product.tax());
        productItem.setDiscount(product.discount());
        productItem.setTotalPrice(product.totalPrice());
        productItem.setStatusCode(product.statusCode());
        productItem.setCreatedBy(userId);
        productItem.setCreatedOn(LocalDateTime.now());
        productItem.setPurchaseOrder(updatedPurchaseOrder);
        return productItem;
    }

    private PurchaseOrder setPurchaseOrderDetailsForUpdate(PurchaseOrder purchaseOrder,
                                                           UpdatePurchaseOrderRecord purchaseOrderRecord, String userId) {
        purchaseOrder.setSubtotalAmount(purchaseOrderRecord.subtotalAmount());
        purchaseOrder.setShippingAmount(purchaseOrderRecord.shippingAmount());
        purchaseOrder.setTax(purchaseOrderRecord.tax());
        purchaseOrder.setTotalAmount(purchaseOrderRecord.totalAmount());
        purchaseOrder.setStatusCode(purchaseOrderRecord.statusCode());
        purchaseOrder.setUpdatedBy(userId);
        purchaseOrder.setUpdatedOn(LocalDateTime.now());
        purchaseOrder.setWarehouseId(purchaseOrderRecord.warehouseId());
        return purchaseOrder;
    }

    @Override
    public Page<PurchaseOrder> getPurchaseOrders(PurchaseOrderSearch poSearchAttributes,
                                                 PurchaseOrderPage purchaseOrderPage,
                                                 PurchaseOrderListSegregator purchaseOrderListSegregator,
                                                 String userId) {
        log.info("Get all purchase orders");
        if(purchaseOrderListSegregator.isAllList()){
            List<Page<PurchaseOrder>> pages = new ArrayList<>();
            Page<PurchaseOrder> otherPOs =  purchaseOrderListRepository.listOthers(poSearchAttributes, purchaseOrderPage,userId);
            pages.add(otherPOs);
            Page<PurchaseOrder> myPOS =  myPurchaseOrderListRepository.listMy(poSearchAttributes, purchaseOrderPage,userId);
            pages.add(myPOS);
            return combinePages(pages);
        } else if (purchaseOrderListSegregator.isMyList()) {
            return myPurchaseOrderListRepository.listMy(poSearchAttributes, purchaseOrderPage,userId);
        } else if (purchaseOrderListSegregator.isApprover()) {
            return approvalPurchaseOrderListRepository.approverList(poSearchAttributes, purchaseOrderPage);
        }else {
            throw new NotFoundException(HttpStatus.NOT_FOUND,"Purchase orders not found");
        }

        //TODO this is the way I think this logic should do, but stick into the ticket logic
        /*List<Page<PurchaseOrder>> pages = new ArrayList<>();
        if(purchaseOrderListSegregator.isAllList()){
            Page<PurchaseOrder> otherPOs =  purchaseOrderListRepository.listOthers(poSearchAttributes, purchaseOrderPage,userId);
            pages.add(otherPOs);
        }
        if(purchaseOrderListSegregator.isMyList()){
            Page<PurchaseOrder> myPOS =  myPurchaseOrderListRepository.listMy(poSearchAttributes, purchaseOrderPage,userId);
            pages.add(myPOS);
        }
        if(purchaseOrderListSegregator.isApprover()){
            Page<PurchaseOrder> approverPos = approvalPurchaseOrderListRepository.approverList(poSearchAttributes, purchaseOrderPage);
            pages.add(approverPos);
        }

      return combinePages(pages);*/
    }

    private Page<PurchaseOrder> combinePages(List<Page<PurchaseOrder>> pages) {
        List<PurchaseOrder> combinedList = new ArrayList<>();

        for (Page<PurchaseOrder> page : pages) {
            combinedList.addAll(page.getContent());
        }

        // Create a new Page object with the combined list
        Pageable pageable = pages.get(0).getPageable(); // Assuming all pages have the same Pageable
        return new PageImpl<>(combinedList, pageable, combinedList.size());
    }

    @Override
    public String deletePurchaseOrder(String purchaseOrderId, String userId) {
        log.info("purchase order delete start : {}", purchaseOrderId);
       return purchaseOrderRepository.findByIdAndStatusCode(purchaseOrderId,
                PurchaseOrderStatus.DRAFT.name())
               .map(purchaseOrder -> {
                   purchaseOrder.setStatusCode(PurchaseOrderStatus.DELETED.name());
                   purchaseOrder.setDeletedOn(LocalDateTime.now());
                   purchaseOrder.setDeletedBy(userId);
                   purchaseOrderRepository.save(purchaseOrder);
                   return "Purchase order deleted successfully";
               })
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Purchase order not found or not in a DRAFT state"));

    }

    @Override
    public PurchaseOrder getPurchaseOrder(String purchaseOrderId,
                                          PurchaseOrderListSegregator purchaseOrderListSegregator, String userId) {
        log.info("Get purchase order by id {}", purchaseOrderId);
        if(purchaseOrderListSegregator.isAllList()){
            log.debug("Get purchase order by id for list_all_purchase_order");
           return purchaseOrderRepository.findById(purchaseOrderId)
                    .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Purchase order not found"));
        } else if (purchaseOrderListSegregator.isMyList()) {
            log.debug("Get purchase order by id for list_my_purchase_order");
            return purchaseOrderRepository.findByIdAndUserId(purchaseOrderId, userId)
                    .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Purchase order not found"));
        }else {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Purchase order not found");
        }


        //TODO in the ticket logic is wrong
        /*if(purchaseOrderListSegregator.isApprover()){
            log.debug("Get purchase order by id for approve_purchase_order");
            return purchaseOrderRepository.findByPoNumberAndStatusCode(purchaseOrderId, PurchaseOrderStatus.PENDING_AUTHORIZATION.name())
                    .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Purchase order not found"));
        }*/

    }

    @Override
    public PurchaseOrderStatusResponse submitPurchaseOrder(String purchaseOrderId, String userId) {
       return purchaseOrderRepository.findByIdAndStatusCode(purchaseOrderId,PurchaseOrderStatus.DRAFT.name())
                .map(purchaseOrder -> {
                    purchaseOrder.setPoNumber(purchaseOrderId);
                    purchaseOrder.setStatusCode(PurchaseOrderStatus.PENDING_AUTHORIZATION.name());
                    purchaseOrderRepository.save(purchaseOrder);
                    //TODO set submitted by and submitted on
                    return new PurchaseOrderStatusResponse(purchaseOrderId,purchaseOrder.getStatusCode());
                }).orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Purchase order not found or not in a DRAFT state"));

    }

    @Override
    public PurchaseOrderStatusResponse approvePurchaseOrder(String purchaseOrderId, String userId,
                                                            ApproveOrderRequest approveOrderRequest) {
        return purchaseOrderRepository.findByIdAndStatusCode(purchaseOrderId,PurchaseOrderStatus.PENDING_AUTHORIZATION.name())
                .map(purchaseOrder -> {
                    purchaseOrder.setPoNumber(purchaseOrderId);
                    purchaseOrder.setStatusCode(approveOrderRequest.status_code());
                    purchaseOrderRepository.save(purchaseOrder);
                    //TODO set approved by and approved on
                    return new PurchaseOrderStatusResponse(purchaseOrderId,purchaseOrder.getStatusCode());
                }).orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Purchase order not found or not in a Pending state"));
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    @Override
    public Page<PurchaseOrder> getPurchaseOrdersBySupplierId(PurchaseOrderSearch poSearchAttributes, PurchaseOrderPage purchaseOrderPage, String supplier_id) {
        log.info("Get purchase order by supplier id");
        return purchaseOrderSupplierRepository.listBySupplierId(poSearchAttributes,purchaseOrderPage,supplier_id);
    }

    @Override
    public PurchaseOrderReceiveResponse receivePurchaseOrder(String po_id, PurchaseOrderReceiveRequest purchaseOrderReceiveRequest, String userId) {
        log.info("Receive purchase order start {}", po_id);
        List<StockReceiptItem> stockReceiptItems =  new ArrayList<>();
        StockReceipt stockReceipt =  new StockReceipt();

        //Check purchase order exist by id
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(po_id)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Purchase order not found for " + po_id));

        // Check received date
        if(purchaseOrderReceiveRequest.received_date().isAfter(LocalDateTime.now())){
            throw new CommonException(HttpStatus.BAD_REQUEST, "Received date should not be a future date");
        }

        if(purchaseOrderReceiveRequest.received_products_list().isEmpty()){
            // Check products
            throw new CommonException(HttpStatus.BAD_REQUEST, "Product list should be present");
        }

        // Check warehouse
        purchaseOrderReceiveRequest.received_products_list()
                .forEach(item -> {
                            if(!purchaseOrder.getWarehouseId().equals(item.warehouse_id())){
                                throw new CommonException(HttpStatus.BAD_REQUEST, "Warehouse id "+ item.warehouse_id() + " not match with purchase order");
                            }
                        });

        // Check quantity
        purchaseOrderReceiveRequest.received_products_list()
                .forEach(item -> purchaseOrderProductRepository.findByProdId(item.prod_id())
                        .ifPresentOrElse(productItemList -> productItemList.forEach(product -> {
                           if (product.getQuantity() < item.quantity_received()) {
                                throw new CommonException(HttpStatus.BAD_REQUEST, "Received quantity higher than the ordered");
                            } else if (product.getQuantity().equals(item.quantity_received())) {
                                log.debug("Stock receipt full received flow");
                                // Call create with full received
                                stockReceiptItems.add(setStockReceiptItem(item, purchaseOrderReceiveRequest.received_date(), userId));
                                stockReceipt.setStatusCode(PurchaseOrderStatus.RECEIVED.name());
                                stockReceipt.setReceiptItems(stockReceiptItems);
                            } else {
                                log.debug("Stock receipt partial received flow");
                                // Call create with partial received
                                stockReceiptItems.add(setStockReceiptItem(item, purchaseOrderReceiveRequest.received_date(), userId));
                                stockReceipt.setStatusCode(PurchaseOrderStatus.PARTIALLY_RECEIVED.name());
                                stockReceipt.setReceiptItems(stockReceiptItems);
                            }
                        }), () -> {
                            throw new NotFoundException(HttpStatus.NOT_FOUND, "Product " + item.prod_id() + " not found");
                        }));

        // Set receipt
        stockReceipt.setPurchaseOrderId(po_id);
        stockReceipt.setReceiptItems(stockReceiptItems);
        stockReceipt.setPoReceivedDate(purchaseOrderReceiveRequest.received_date());
        stockReceipt.setCreatedBy(userId);
        stockReceipt.setCreatedOn(LocalDateTime.now());
        StockReceipt receipt = stockReceiptRepository.save(stockReceipt);
        log.info("Receive purchase order success {}", po_id);
        return new PurchaseOrderReceiveResponse(po_id, receipt.getId());
    }

    private StockReceiptItem setStockReceiptItem(ReceivedProducts receiptItem, LocalDateTime receivedDate, String userId){
        StockReceiptItem stockReceiptItem =  new StockReceiptItem();
        stockReceiptItem.setProductId(receiptItem.prod_id());
        stockReceiptItem.setWarehouseId(receiptItem.warehouse_id());
        stockReceiptItem.setQuantityReceived(receiptItem.quantity_received());
        stockReceiptItem.setQuantityFlag(receiptItem.check_quantity_flag());
        stockReceiptItem.setSerialNumbers(receiptItem.serial_number_list());
        stockReceiptItem.setExpiryDate(receiptItem.expiry_date());
        stockReceiptItem.setReceivedDate(receivedDate);
        stockReceiptItem.setReceivedBy(userId);
        stockReceiptItem.setCreatedBy(userId);
        stockReceiptItem.setCreatedOn(LocalDateTime.now());
        return stockReceiptItem;
    }
}


