package com.qortex.inventory;

import com.qortex.inventory.common.enums.AssigneeType;
import com.qortex.inventory.common.enums.PurchaseOrderStatus;
import com.qortex.inventory.common.enums.WarehouseStatus;
import com.qortex.inventory.common.exception.*;
import com.qortex.inventory.dto.*;

import com.qortex.inventory.model.*;
import com.qortex.inventory.repository.*;
import com.qortex.inventory.service.impl.InventoryWarehouseManagementServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("dev")
public class InventoryWarehouseManagementServiceTest {
	@InjectMocks
	InventoryWarehouseManagementServiceImpl inventoryWarehouseManagementService;

	@Mock
	InventoryListRepository inventoryListRepository;
	@Mock
	InventoryWarehouseRepository inventoryWarehouseRepository;
	@Mock
	InventoryWarehouseAddressRepository inventoryWarehouseAddressRepository;

	@Mock
	private PurchaseOrderRepository purchaseOrderRepository;

	@Mock
	private PurchaseOrderListRepository purchaseOrderListRepository;

	@Mock
	private SupplierRepository supplierRepository;

	@Mock
	private PurchaseOrderProductRepository purchaseOrderProductRepository;

	@Mock
	private MyPurchaseOrderListRepository myPurchaseOrderListRepository;

	@Mock
	private ApprovalPurchaseOrderListRepository approvalPurchaseOrderListRepository;

	@Mock
	private PurchaseOrderSupplierRepository purchaseOrderSupplierRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private StockReceiptRepository stockReceiptRepository;

	Warehouse inventory;
	WarehouseAddress address;
	WarehouseRequest warehouseRequest;
	InventoryWarehouseResponse response;
	DeleteWarehouse deleteWarehouse1;


	@BeforeEach
	void initializeInventoryWarehouse() {
		inventory = new Warehouse();
		inventory.setWarehouseName("sampleWarehouseName");
		inventory.setWarehouseCode("sampleWarehouseCode");
		inventory.setWarehouseType("sampleWarehouseType");
		inventory.setAssignedId("6E843862-6DE1-41B1-AE5F-B20D367C3422");
		inventory.setStatusCode("ACTIVE");

		address = new WarehouseAddress();
		address.setDoorName("sampleDoorName");
		address.setBuildingName("sampleBuildingName");
		address.setStreet("sampleStreet");
		address.setCity("sampleCity");
		address.setState("sampleState");
		address.setCountry("sampleCountry");
		address.setZipCode("sampleZipCode");
		address.setStatusCode("ACTIVE");

		response = new InventoryWarehouseResponse();
		response.setWarehouseId("C75F8C0D-701D-42FB-8F18-7A926F22013B");
		response.setWarehouseName("sampleWarehouseName");

		String warehouseName = "sample-warehouseName";
		String warehouseCode = "sample-warehouseCode";
		String warehouseType = "sample-warehouseType";
		String assignedId = "sample assignedId";
		String doorName = "sample doorName";
		String buildingName = "sample buildingName";
		String street = "sample street";
		String city = "simplicity";
		String state = "sample-state";
		String country = "sample country";
		String zipCode = "sampleCode";
		String warehouseStatusCode = "sampleACTIVE";
		String warehouseAddressStatusCode = "sampleACTIVE";

		warehouseRequest = new WarehouseRequest(warehouseName, warehouseCode, warehouseType, assignedId, doorName,
				buildingName, street, city, state, country, zipCode, warehouseStatusCode, warehouseAddressStatusCode);
	}

	@BeforeEach
	void initializeInventoryDeleteWarehouse() {
		List<String> warehouseBatch = Arrays.asList("60DE80DF-7C67-4AF3-B48D-B77BB0A620ED",
				"2EF8A9C2-17AC-4AA5-897B-7E68505C3F64");
		deleteWarehouse1 = new DeleteWarehouse();
		deleteWarehouse1.setWarehouse_id(warehouseBatch);
	}

	@BeforeEach
	void initializeWarehouseRequest() {

		String warehouseName = "sample-warehouseName";
		String warehouseCode = "sample-warehouseCode";
		String warehouseType = "sample-warehouseType";
		String assignedId = "sample assignedId";
		String doorName = "sample doorName";
		String buildingName = "sample buildingName";
		String street = "sample street";
		String city = "simplicity";
		String state = "sample-state";
		String country = "sample country";
		String zipCode = "sampleCode";
		String warehouseStatusCode = "sampleACTIVE";
		String warehouseAddressStatusCode = "sampleACTIVE";

		warehouseRequest = new WarehouseRequest(warehouseName, warehouseCode, warehouseType, assignedId, doorName,
				buildingName, street, city, state, country, zipCode, warehouseStatusCode, warehouseAddressStatusCode);
	}

	@Test
	void addInventoryWarehouseTest() throws WareHouseDetailsNotFoundException {
		String userId = "C75F8C0D-701D-42FB-8F18-7A926F22013B";
		when(inventoryWarehouseRepository.save(any(Warehouse.class))).thenReturn(inventory);
		Warehouse inventory = inventoryWarehouseManagementService.addInventoryWarehouse(warehouseRequest, userId);
		Assertions.assertEquals("sampleWarehouseName", inventory.getWarehouseName());

	}

	@Test
	void checkValidationForAllConstraintTest() {

		String warehouseName = "sample-warehouseName";
		String warehouseCode = "sample-warehouseCode";
		String warehouseType = "sample-warehouseType";
		String assignedId = "sample assignedId";
		String doorName = "sample doorName";
		String buildingName = "sample buildingName";
		String street = "sample street";
		String city = "sample-city";
		String state = "sample-state";
		String country = "sample country";
		String zipCode = "sampleCode";
		String warehouseStatusCode = "sampleACTIVE";
		String warehouseAddressStatusCode = "sampleACTIVE";

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		warehouseRequest = new WarehouseRequest(warehouseName, warehouseCode, warehouseType, assignedId, doorName,
				buildingName, street, city, state, country, zipCode, warehouseStatusCode, warehouseAddressStatusCode);

		Set<ConstraintViolation<WarehouseRequest>> constraintViolations = validator.validate(warehouseRequest);
		assertEquals(0, constraintViolations.size());
	}

	@Test
	void checkValidationForFailedConstraintTest() {

		String warehouseName = "";
		String warehouseCode = "sample-warehouseCode";
		String warehouseType = "sample-warehouseType";
		String assignedId = "sample assignedId";
		String doorName = "sample doorName";
		String buildingName = "sample buildingName";
		String street = "sample street";
		String city = "simplicity";
		String state = "sample-state";
		String country = "sample country";
		String zipCode = "sampleCode";
		String warehouseStatusCode = "sampleACTIVE";
		String warehouseAddressStatusCode = "sampleACTIVE";

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		warehouseRequest = new WarehouseRequest(warehouseName, warehouseCode, warehouseType, assignedId, doorName,
				buildingName, street, city, state, country, zipCode, warehouseStatusCode, warehouseAddressStatusCode);

		Set<ConstraintViolation<WarehouseRequest>> constraintViolations = validator.validate(warehouseRequest);
		assertEquals(1, constraintViolations.size());
	}

	@Test
	@DisplayName("Update warehouse not exist")
	public void updateWareHouseNotExistTest() {
		String status = "Active";
		String warehouseId = "A6C90C39-224C-4461-A69B-7A25719EAB44";
		String userId = "6E843862-6DE1-41B1-AE5F-B20D367C3422";

		String warehouseName = "Warehouse colombo";
		String warehouseCode = "EEEE6F";
		String warehouseType = "Building";
		String assignedId = "6E843862-6DE1-41B1-AE5F-B20D367C3455";
		String doorName = "No421";
		String buildingName = "parkland";
		String street = "park street";
		String city = "Colombo";
		String state = "Western";
		String country = "Sri Lanka";
		String zipCode = "11010";
		String warehouseStatusCode = "Active";
		String warehouseAddressStatusCode = "Active";

		WarehouseRequest request = new WarehouseRequest(warehouseName, warehouseCode, warehouseType, assignedId,
				doorName, buildingName, street, city, state, country, zipCode, warehouseStatusCode,
				warehouseAddressStatusCode);

		when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(warehouseId, status))
				.thenReturn(Optional.empty());
		WareHouseDetailsNotFoundException exception = Assertions.assertThrows(WareHouseDetailsNotFoundException.class,
				() -> inventoryWarehouseManagementService.updateWareHouse(warehouseId, request, userId),
				"Warehouse not found for this id : A6C90C39-224C-4461-A69B-7A25719EAB44");

		Assertions.assertTrue(exception.getMessage()
				.equalsIgnoreCase("Warehouse not found for this id : A6C90C39-224C-4461-A69B-7A25719EAB44"));

		Assertions.assertTrue(exception.getErrorCode().is4xxClientError());

	}

	@Test
	@DisplayName("Update warehouse")
	public void updateWareHouseTest() {

		String userId="6E843862-6DE1-41B1-AE5F-B20D367C3422";
		String wareID="A6C90C39-224C-4461-A69B-7A25719EAB44";
		WarehouseAddress add=new WarehouseAddress();
		Warehouse req=new Warehouse();
		req.setId("A6C90C39-224C-4461-A69B-7A25719EAB44");
		req.setWarehouseName("testName");
		req.setWarehouseCode("testCode");
		req.setWarehouseType("testType");
		req.setStatusCode("ACTIVE");
		req.setAssignedId("6E843862-6DE1-41B1-AE5F-B20D367C3455");

		add.setDoorName("testDoor");
		add.setBuildingName("testBuilding");
		add.setStreet("123");
		add.setZipCode("23453330");
		add.setStatusCode("ACTIVE");
		req.setAddress(add);

		String warehouseName = "Warehouse colombo";
		String warehouseCode = "EEEE6F";
		String warehouseType = "Building";
		String assignedId = "6E843862-6DE1-41B1-AE5F-B20D367C3455";
		String doorName = "No421";
		String buildingName = "parkland";
		String street = "park street";
		String city = "Colombo";
		String state = "Western";
		String country = "Sri Lanka";
		String zipCode = "11010";
		String warehouseStatusCode = "Active";
		String warehouseAddressStatusCode = "Active";

		WarehouseRequest request = new WarehouseRequest(warehouseName, warehouseCode, warehouseType, assignedId,
				doorName, buildingName, street, city, state, country, zipCode, warehouseStatusCode,
				warehouseAddressStatusCode);

		Mockito.when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(wareID, WarehouseStatus.DELETED.name()))
				.thenReturn(Optional.of(req));
		Mockito.when(inventoryWarehouseRepository.save(req)).thenReturn(req);
		Warehouse response= inventoryWarehouseManagementService.updateWareHouse(wareID,warehouseRequest,userId);
		Assertions.assertNotNull(response);

	}

	@Test
	@DisplayName("Delete warehouse Empty list test")
	public void deleteWareHouseEmptyTest() {
		String status = "DELETE";
		String userId = "6E843862-6DE1-41B1-AE5F-B20D367C3422";
		DeleteWarehouse deleteWarehouse = new DeleteWarehouse();
		deleteWarehouse.setWarehouse_id(Arrays.asList(""));
		Mockito.when(inventoryWarehouseRepository.deleteWarehouse(status, LocalDateTime.now(), userId,
				deleteWarehouse.getWarehouse_id())).thenReturn(1);
		Mockito.when(inventoryWarehouseAddressRepository.deleteWarehouseAddress(status, LocalDateTime.now(), userId,
				deleteWarehouse.getWarehouse_id())).thenReturn(1);

		Assertions.assertThrows(WarehouseDeletionException.class, () -> {
			inventoryWarehouseManagementService.deleteWarehouse(deleteWarehouse, userId);
		});
	}


	@Test
	@DisplayName("Warehouse Id not found test")
	public void getWareHouseByIdNotFoundTest() {
		String warehouseId = "C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD";
		String status = "Active";
		String warehouseType = AssigneeType.ENTERPRISE.name();
		when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(warehouseId, status))
				.thenReturn(Optional.empty());

		WareHouseDetailsNotFoundException exception = Assertions.assertThrows(WareHouseDetailsNotFoundException.class,
				() -> inventoryWarehouseManagementService.getWarehouseById(warehouseType, warehouseId),
				"Warehouse not found for this id : C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD");
		Assertions.assertNotNull(exception);
		Assertions.assertEquals("Warehouse not found for this id :" + warehouseId, exception.getMessage());
	}

	@Test
	public void testGetWarehouseById() throws WareHouseDetailsNotFoundException {
		// test data
		String warehouseId = "6E843862-6DE1-41B1-AE5F-B20D367C3422";
		Warehouse warehouse = new Warehouse();
		String warehouseType = AssigneeType.SALESMAN.name();
		warehouse.setId(warehouseId);
		warehouse.setStatusCode(WarehouseStatus.DELETED.name());
		warehouse.setAssignedId("CDA39ED5-5CDB-4DE2-8B7E-FF8C3699F161");
		warehouse.setAddress(address);
		// mock the repository method
		when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(Mockito.eq(warehouseId),
				Mockito.eq(WarehouseStatus.DELETED.name()))).thenReturn(Optional.of(warehouse));

		// call the service method
		WarehouseResponse result = inventoryWarehouseManagementService.getWarehouseById(warehouseType, warehouseId);

		// verify the repository method was called with the correct parameters
		Mockito.verify(inventoryWarehouseRepository).findByIdAndStatusCodeNot(Mockito.eq(warehouseId),
				Mockito.eq(WarehouseStatus.DELETED.name()));

		// verify the result is not null and has the correct values
		Assertions.assertNotNull(result);
		assertEquals(warehouseId, result.getWarehouseId());
	}


	@Test
	@DisplayName("Delete warehouse")
	public void deleteWareHouseTest() {

		DeleteWarehouse deleteList = new DeleteWarehouse();
		deleteList.setWarehouse_id(Arrays.asList("6E843862-6DE1-41B1-AE5F-B20D367C3422",
				"6E843862-6DE1-41B1-AE5F-B20D367C3422", "6E843862-6DE1-41B1-AE5F-B20D367C3422"));
		String userId = "6E843862-6DE1-41B1-AE5F-B20D367C3422";

		when(inventoryWarehouseRepository.deleteWarehouse(anyString(), any(LocalDateTime.class),
				Mockito.eq(userId), Mockito.anyList())).thenReturn(3);
		when(inventoryWarehouseAddressRepository.deleteWarehouseAddress(anyString(), any(LocalDateTime.class),
				Mockito.eq(userId), Mockito.anyList())).thenReturn(3);

		String result = inventoryWarehouseManagementService.deleteWarehouse(deleteList, userId);
		verify(inventoryWarehouseRepository, times(1)).deleteWarehouse(anyString(),
				any(LocalDateTime.class), Mockito.eq(userId), Mockito.anyList());
		verify(inventoryWarehouseAddressRepository, times(1)).deleteWarehouseAddress(anyString(),
				any(LocalDateTime.class), Mockito.eq(userId), Mockito.anyList());
		Assertions.assertEquals("Delete successfully.", result);
	}

	@Test
	void testGetAllInventories() {
		// Arrange
		String warehouseName = "Warehouse";
		String warehouseType = "Type";
		String warehouseCode = "Code";
		Integer offset = 0;
		Integer pageSize = 10;

		Pageable pageable = PageRequest.of(offset, pageSize);
		InventorySearchCriteria inventorySearchCriteria = new InventorySearchCriteria(
				warehouseName,
				warehouseCode,
				warehouseType
		);
		List<Warehouse> warehouses = new ArrayList<>();
		warehouses.add(new Warehouse());
		Page<Warehouse> expectedPage = new PageImpl<>(warehouses, pageable, warehouses.size());
		Mockito.when(inventoryListRepository.findAllWarehouses(Mockito.any(InventoryPage.class),
				Mockito.any(InventorySearchCriteria.class))).thenReturn(expectedPage);
		// Act
		Page<WarehouseResponse> actualPage = inventoryWarehouseManagementService.getAllInventories(warehouseName, warehouseType, warehouseCode, offset, pageSize);
		// Assert
		assertNotNull(actualPage);
		Assertions.assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
		Assertions.assertEquals(expectedPage.getNumberOfElements(), actualPage.getNumberOfElements());
		Assertions.assertEquals(expectedPage.getContent().size(), actualPage.getContent().size());
	}

	@Test
	@DisplayName("Warehouse found by Id and Status")
	public void getWareHouseByIdTest() {
		String warehouseId = "C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD";
		String userId = "6E843862-6DE1-41B1-AE5F-B20D367C3422";
		String status = "DELETE";
		Warehouse warehouse = new Warehouse();
		warehouse.setId("C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD");
		warehouse.setWarehouseName("sampleWarehouseName");
		warehouse.setWarehouseType("Building");
		warehouse.setWarehouseCode("00006FF");
		warehouse.setStatusCode("Delete");
		warehouse.setAddress(address);
		warehouse.setCreatedOn(LocalDateTime.now());
		warehouse.setCreatedBy(userId);
		Mockito.when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(warehouseId, status))
				.thenReturn(Optional.of(warehouse));
		Assertions.assertEquals("sampleWarehouseName", warehouse.getWarehouseName());
		Assertions.assertEquals("00006FF", warehouse.getWarehouseCode());
	}

	@Test
	@DisplayName("Inventory warehouse name already exist test")
	public void addWarehouseWithExistingWarehouseName() {

		String userId = "C75F8C0D-701D-42FB-8F18-7A926F22013B";

		Warehouse existingInventory = new Warehouse();
		existingInventory.setWarehouseName("testWarehouse");
		existingInventory.setWarehouseCode("sampleWarehouseCode");
		existingInventory.setWarehouseType("sampleWarehouseType");
		existingInventory.setAssignedId("6E843862-6DE1-41B1-AE5F-B20D367C3422");
		existingInventory.setStatusCode(WarehouseStatus.DELETED.name());

		String warehouseName = "testWarehouse";
		String warehouseCode = "test-warehouseCode";
		String warehouseType = "test-warehouseType";
		String assignedId = "test assignedId";
		String doorName = "test doorName";
		String buildingName = "test buildingName";
		String street = "test street";
		String city = "testCity";
		String state = "test-state";
		String country = "test country";
		String zipCode = "testCode";
		String warehouseStatusCode = WarehouseStatus.DELETED.name();
		String warehouseAddressStatusCode = WarehouseStatus.DELETED.name();

		WarehouseRequest testWarehouseRequest = new WarehouseRequest(warehouseName, warehouseCode, warehouseType, assignedId, doorName,
				buildingName, street, city, state, country, zipCode, warehouseStatusCode, warehouseAddressStatusCode);

		when(inventoryWarehouseRepository.findByWarehouseNameAndStatusCodeNot(warehouseName,
				WarehouseStatus.DELETED.name())).thenReturn(Optional.of(existingInventory));

		InventoryNameAlreadyExist exception = Assertions.assertThrows(InventoryNameAlreadyExist.class, () -> {
			inventoryWarehouseManagementService.addInventoryWarehouse(testWarehouseRequest, userId);
		});
		Assertions.assertEquals(HttpStatus.CONFLICT, exception.getErrorCode());
		Assertions.assertEquals("Warehouse name already exist", exception.getMessage());
	}


	@Test
	void createPurchaseOrder() {
		// Prepare test data
		String userId = "testUserId";
		PurchaseOrderProductItemRecord itemRecord = new PurchaseOrderProductItemRecord("Product", "Sample product", BigDecimal.valueOf(10.0), "FCF17214-3BE6-4CBF-854D-1D48C0F8C450", 2, BigDecimal.valueOf(14.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0),10, "ACTIVE");
		List<PurchaseOrderProductItemRecord> productItems = List.of(itemRecord);
		PurchaseOrderRecord purchaseOrderRecord = new PurchaseOrderRecord("testEntityId", "7DC299C2-7E4D-44D5-AA1C-1A459312B9AE", LocalDate.now(),
				LocalDate.now(), "testUserId", "testWarehouseId", BigDecimal.valueOf(100.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0), "note", "ACTIVE", "12121",productItems);
		Supplier supplier = new Supplier();
		supplier.setId("7DC299C2-7E4D-44D5-AA1C-1A459312B9AE");
		supplier.setSupplierNameEn("testSupplierName");
		// Mock repository behavior
		when(supplierRepository.findById("7DC299C2-7E4D-44D5-AA1C-1A459312B9AE"))
				.thenReturn(Optional.of(supplier));
		when(purchaseOrderRepository.save(any(PurchaseOrder.class)))
				.thenAnswer(invocation -> {
					PurchaseOrder savedPurchaseOrder = invocation.getArgument(0);
					savedPurchaseOrder.setId("testPoId");
					return savedPurchaseOrder;
				});

		// Call the method under test
		String result = inventoryWarehouseManagementService.createPurchaseOrder(purchaseOrderRecord, userId);

		// Verify the result
		assertNotNull(result);
	}

	@Test
	void createPurchaseOrderWithEmptyProducts() {
		// Prepare test data
		String userId = "testUserId";
		PurchaseOrderRecord purchaseOrderRecord = new PurchaseOrderRecord("testEntityId", "7DC299C2-7E4D-44D5-AA1C-1A459312B9AE", LocalDate.now(),
				LocalDate.now(), "testUserId", "testWarehouseId", BigDecimal.valueOf(100.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0), "note", "ACTIVE", "1212",null);
		Supplier supplier = new Supplier();
		supplier.setId("7DC299C2-7E4D-44D5-AA1C-1A459312B9AE");
		supplier.setSupplierNameEn("testSupplierName");
		// Mock repository behavior
		when(supplierRepository.findById("7DC299C2-7E4D-44D5-AA1C-1A459312B9AE"))
				.thenReturn(Optional.of(supplier));
		ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			inventoryWarehouseManagementService.createPurchaseOrder(purchaseOrderRecord, userId);
		});
		Assertions.assertEquals("Products cannot be empty for creating purchase order", exception.getMessage());
	}

	@Test
	void createPurchaseOrderWithSupplierNotExist() {
		// Prepare test data
		String userId = "testUserId";
		PurchaseOrderRecord purchaseOrderRecord = new PurchaseOrderRecord("testEntityId", "testPoNumber", LocalDate.now(),
				LocalDate.now(), "testUserId", "testWarehouseId", BigDecimal.valueOf(100.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0), "note", "ACTIVE", "1234",null);

		ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			inventoryWarehouseManagementService.createPurchaseOrder(purchaseOrderRecord, userId);
		});
		Assertions.assertEquals("Supplier not exist", exception.getMessage());
	}

	@Test
	void createPurchaseOrderEmpty() {
		// Prepare test data
		String userId = "testUserId";
		PurchaseOrderRecord purchaseOrderRecord = new PurchaseOrderRecord("testEntityId", "testPoNumber", LocalDate.now(),
				LocalDate.now(), "testUserId", "testWarehouseId", BigDecimal.valueOf(100.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0), "note", "ACTIVE", "1213",null);

		ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			inventoryWarehouseManagementService.createPurchaseOrder(null, userId);
		});
		Assertions.assertEquals("Cannot create purchase order", exception.getMessage());
	}

	@Test
	@DisplayName("Warehouse found by Id and Status")
	public void getWareHouseByIdTestAddressNotFound() {
		String warehouseType = "SALESMAN";
		String warehouseId = "C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD";
		String userId = "6E843862-6DE1-41B1-AE5F-B20D367C3422";
		Warehouse warehouse = new Warehouse();
		warehouse.setId("C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD");
		warehouse.setWarehouseName("sampleWarehouseName");
		warehouse.setWarehouseType("Building");
		warehouse.setWarehouseCode("R20006FF");
		warehouse.setStatusCode("Delete");
		warehouse.setAddress(null);
		warehouse.setCreatedOn(LocalDateTime.now());
		warehouse.setCreatedBy(userId);
		Mockito.when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(warehouseId, WarehouseStatus.DELETED.name()))
				.thenReturn(Optional.of(warehouse));

		WareHouseDetailsNotFoundException exception = Assertions.assertThrows(WareHouseDetailsNotFoundException.class , () -> {
			inventoryWarehouseManagementService.getWarehouseById(warehouseType, warehouseId);
		});
		Assertions.assertNotNull(exception);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getErrorCode());
		Assertions.assertEquals("Assign Id is not present for this warehouse", exception.getMessage());

	}

	@Test
	void getWarehouseByIdWithTypeSalesmanTest(){
		String warehouseId = "C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD";
		Warehouse warehouse = new Warehouse();
		warehouse.setId("C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD");
		warehouse.setWarehouseName("sampleWarehouseName");
		warehouse.setWarehouseType("Building");
		warehouse.setWarehouseCode("00006FF");
		warehouse.setAssignedId("1FCD0176-AABD-44C5-A60A-0F18F18FFA2C");
		warehouse.setAddress(address);

		Mockito.when(inventoryWarehouseRepository.getUserAssignName(warehouseId)).thenReturn("SalesmanUser");
		Mockito.when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(warehouseId, WarehouseStatus.DELETED.name()))
				.thenReturn(Optional.of(warehouse));

		WarehouseResponse response= inventoryWarehouseManagementService.getWarehouseById(AssigneeType.SALESMAN.name(), warehouseId);
		response.setAssignUserName(AssigneeType.SALESMAN.name());
		Assertions.assertEquals(AssigneeType.SALESMAN.name(), response.getAssignUserName());
		Assertions.assertEquals("00006FF", response.getWarehouseCode());

	}

	@Test
	void getWarehouseByIdWithTypeEnterpriseTest(){
		String warehouseId = "C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD";
		Warehouse warehouse = new Warehouse();
		warehouse.setId("C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD");
		warehouse.setWarehouseName("sampleWarehouseName");
		warehouse.setWarehouseType("Building");
		warehouse.setWarehouseCode("00006FFR3");
		warehouse.setAssignedId("512B76D7-94CD-4353-AB9D-82B9CFE7BE04");
		warehouse.setAddress(address);

		Mockito.when(inventoryWarehouseRepository.getUserAssignEnterpriseName(warehouseId)).thenReturn("EnterpriseAdmin");
		Mockito.when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(warehouseId, WarehouseStatus.DELETED.name()))
				.thenReturn(Optional.of(warehouse));
		WarehouseResponse response= inventoryWarehouseManagementService.getWarehouseById(AssigneeType.ENTERPRISE.name(), warehouseId);
		response.setAssignUserName(AssigneeType.ENTERPRISE.name());
		Assertions.assertEquals(AssigneeType.ENTERPRISE.name(), response.getAssignUserName());
		Assertions.assertEquals("00006FFR3", response.getWarehouseCode());
	}

	@Test
	void getWarehouseByIdWithTypeDigitalTest(){
		String warehouseId = "C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD";
		Warehouse warehouse = new Warehouse();
		warehouse.setId("C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD");
		warehouse.setWarehouseName("DIGITAL");
		warehouse.setWarehouseType("Building");
		warehouse.setWarehouseCode("00006FFR3");
		warehouse.setAssignedId("512B76D7-94CD-4353-AB9D-82B9CFE7BE04");
		warehouse.setAddress(address);

		Mockito.when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(warehouseId, WarehouseStatus.DELETED.name()))
				.thenReturn(Optional.of(warehouse));
		WarehouseResponse response= inventoryWarehouseManagementService.getWarehouseById(AssigneeType.DIGITAL.name(), warehouseId);
		Assertions.assertEquals("DIGITAL", response.getWarehouseName());
		Assertions.assertEquals("00006FFR3", response.getWarehouseCode());

	}

	@Test
	void getWarehouseByIdWithTypePhysicalTest() {
		String warehouseId = "C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD";
		Warehouse warehouse = new Warehouse();
		warehouse.setId("C9B136E7-342F-4DE8-BFEA-54C9ED4BA9AD");
		warehouse.setWarehouseName("PHYSICAL");
		warehouse.setWarehouseType("Building");
		warehouse.setWarehouseCode("00006FFR3");
		warehouse.setAssignedId("512B76D7-94CD-4353-AB9D-82B9CFE7BE04");
		warehouse.setAddress(address);

		Mockito.when(inventoryWarehouseRepository.findByIdAndStatusCodeNot(warehouseId, WarehouseStatus.DELETED.name()))
				.thenReturn(Optional.of(warehouse));
		WarehouseResponse response = inventoryWarehouseManagementService.getWarehouseById(AssigneeType.PHYSICAL.name(), warehouseId);
		Assertions.assertEquals("PHYSICAL", response.getWarehouseName());
		Assertions.assertEquals("00006FFR3", response.getWarehouseCode());

	}

	@Test
	@DisplayName("Update purchase order, with add new product")
	void updatePurchaseOrder() {

		String purchaseOrderId = "4F084AC9-2F1A-4507-B33F-E93033571867";
		String userId = "testUserId";

		PurchaseOrderProductItemRecord itemRecord = new PurchaseOrderProductItemRecord("Product", "Sample product",BigDecimal.valueOf(10.0),
				"4F084AC9-2F1A-4507-B33F-E93033571867", 2, BigDecimal.valueOf(14.0), BigDecimal.valueOf(10.0),
				BigDecimal.valueOf(10.0), 10,PurchaseOrderStatus.SAVED.name());
		List<PurchaseOrderProductItemRecord> productItems = List.of(itemRecord);
		UpdatePurchaseOrderRecord purchaseOrderRecord = new UpdatePurchaseOrderRecord("testEntityId", "testPoNumber", "testSupplierId", LocalDateTime.now(),
				LocalDateTime.now(), "testUserId", "testWarehouseId", BigDecimal.valueOf(100.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0),
				BigDecimal.valueOf(10.0), "Notes", PurchaseOrderStatus.SAVED.name(), productItems);

		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setId(purchaseOrderId);
		purchaseOrder.setSubtotalAmount(BigDecimal.valueOf(50.0));
		purchaseOrder.setShippingAmount(BigDecimal.valueOf(5.0));
		purchaseOrder.setTax(BigDecimal.valueOf(50.0));
		purchaseOrder.setTotalAmount(BigDecimal.valueOf(50.0));
		purchaseOrder.setStatusCode(PurchaseOrderStatus.SAVED.name());

		PurchaseOrderProductItem productItem = new PurchaseOrderProductItem();
		productItem.setProdId("4F084AC9-2F1A-4507-B33F-E93033571867");
		productItem.setUnitPrice(BigDecimal.valueOf(5.0));
		productItem.setQuantity(2);
		productItem.setTax(BigDecimal.valueOf(5.0));
		productItem.setDiscount(BigDecimal.valueOf(5.0));
		productItem.setTotalPrice(BigDecimal.valueOf(5.0));
		productItem.setStatusCode(PurchaseOrderStatus.SAVED.name());
		List<PurchaseOrderProductItem> productItemList = new ArrayList<>();
		productItemList.add(productItem);
		purchaseOrder.setPurchaseOrderProductItems(productItemList);

		when(purchaseOrderRepository.findByIdAndStatusCode(purchaseOrderId, PurchaseOrderStatus.SAVED.name())).thenReturn(Optional.of(purchaseOrder));
		when(purchaseOrderProductRepository.findByIdAndStatusCodeNot(purchaseOrderId, PurchaseOrderStatus.DELETED.name()))
				.thenReturn(Optional.of(productItem));
		when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);

		String updatedPurchaseOrderId = inventoryWarehouseManagementService.updatePurchaseOrder(purchaseOrderRecord, userId, purchaseOrderId);

		verify(purchaseOrderRepository, times(1)).findByIdAndStatusCode(purchaseOrderId, PurchaseOrderStatus.SAVED.name());
		verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
		assertEquals(purchaseOrderId, updatedPurchaseOrderId);


		when(purchaseOrderProductRepository.findByIdAndStatusCodeNot(purchaseOrderId, PurchaseOrderStatus.DELETED.name()))
				.thenReturn(Optional.empty());
		when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);

		String updatedPurchaseOrder = inventoryWarehouseManagementService.updatePurchaseOrder(purchaseOrderRecord, userId, purchaseOrderId);

		verify(purchaseOrderRepository, times(2)).findByIdAndStatusCode(purchaseOrderId, PurchaseOrderStatus.SAVED.name());
		verify(purchaseOrderRepository, times(2)).save(any(PurchaseOrder.class));
		assertEquals(purchaseOrderId, updatedPurchaseOrder);

	}

	@Test
	@DisplayName("Update purchase order, not exist")
	void updatePurchaseOrderNotExist() {

		String purchaseOrderId = "4F084AC9-2F1A-4507-B33F-E93033571867";
		String userId = "testUserId";

		PurchaseOrderProductItemRecord itemRecord = new PurchaseOrderProductItemRecord("Product","Sample product",BigDecimal.valueOf(10.0),
				"FCF17214-3BE6-4CBF-854D-1D48C0F8C450", 2, BigDecimal.valueOf(14.0), BigDecimal.valueOf(10.0),
				BigDecimal.valueOf(10.0), 10,PurchaseOrderStatus.SAVED.name());
		List<PurchaseOrderProductItemRecord> productItems = List.of(itemRecord);
		UpdatePurchaseOrderRecord purchaseOrderRecord = new UpdatePurchaseOrderRecord("testEntityId", "testPoNumber", "testSupplierId", LocalDateTime.now(),
				LocalDateTime.now(), "testUserId", "testWarehouseId", BigDecimal.valueOf(100.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(10.0),
				BigDecimal.valueOf(10.0), "Notes", PurchaseOrderStatus.SAVED.name(), productItems);

		when(purchaseOrderRepository.findByIdAndStatusCode(purchaseOrderId,
				PurchaseOrderStatus.SAVED.name())).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			inventoryWarehouseManagementService.updatePurchaseOrder(purchaseOrderRecord, userId, purchaseOrderId);
		});
	}



	@Test
	void testDeletePurchaseOrder() {
		// Mock the repository to return a non-deleted purchase order
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setId("123");
		purchaseOrder.setStatusCode(PurchaseOrderStatus.SAVED.name());
		when(purchaseOrderRepository.findByIdAndStatusCode(anyString(), eq(PurchaseOrderStatus.DRAFT.name())))
				.thenReturn(Optional.of(purchaseOrder));

		// Call the service method
		String result = inventoryWarehouseManagementService.deletePurchaseOrder("123", "user1");

		// Verify that the purchase order was marked as deleted and saved
		verify(purchaseOrderRepository).save(purchaseOrder);
		assertEquals("Purchase order deleted successfully", result);
	}

	@Test
	void testDeletePurchaseOrderNotFound() {
		// Mock the repository to return an empty Optional, indicating that the purchase order was not found
		when(purchaseOrderRepository.findByIdAndStatusCode(anyString(), eq(PurchaseOrderStatus.DRAFT.name())))
				.thenReturn(Optional.empty());

		// Call the service method and expect a ResourceNotFoundException
		assertThrows(ResourceNotFoundException.class, () -> {
			inventoryWarehouseManagementService.deletePurchaseOrder("123", "user1");
		});
	}

	@Test
	void testDeletePurchaseOrderAlreadyDeleted() {
		// Mock the repository to return a deleted purchase order
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setId("123");
		purchaseOrder.setStatusCode(PurchaseOrderStatus.DELETED.name());
		when(purchaseOrderRepository.findByIdAndStatusCode(anyString(), eq(PurchaseOrderStatus.DRAFT.name())))
				.thenReturn(Optional.empty());

		// Call the service method and expect a ResourceNotFoundException
		assertThrows(ResourceNotFoundException.class, () -> {
			inventoryWarehouseManagementService.deletePurchaseOrder("123", "user1");
		});
	}

	@Test
	public void testGetPurchaseOrders_AllList() {
		// Mock the dependencies
		PurchaseOrderSearch poSearchAttributes = new PurchaseOrderSearch("ACTIVE", LocalDate.now(), "7DC299C2-7E4D-44D5-AA1C-1A459312B9AE","12Xysrr");
		PurchaseOrderPage purchaseOrderPage = new PurchaseOrderPage();
		PurchaseOrderListSegregator purchaseOrderListSegregator = new PurchaseOrderListSegregator(true, false, false,true);
		String userId = "C75F8C0D-701D-42FB-8F18-7A926F22013B";

		Page<PurchaseOrder> otherPOs = new PageImpl<>(List.of(new PurchaseOrder(), new PurchaseOrder()));
		Page<PurchaseOrder> myPOs = new PageImpl<>(List.of(new PurchaseOrder()));
		when(purchaseOrderListRepository.listOthers(poSearchAttributes, purchaseOrderPage, userId)).thenReturn(otherPOs);
		when(myPurchaseOrderListRepository.listMy(poSearchAttributes, purchaseOrderPage, userId)).thenReturn(myPOs);

		// Call the method under test
		Page<PurchaseOrder> result = inventoryWarehouseManagementService.getPurchaseOrders(poSearchAttributes,
				purchaseOrderPage,
				purchaseOrderListSegregator,
				userId);

		// Verify the result
		assertEquals(3, result.getContent().size());
	}

    @Test
	public void testGetPurchaseOrders_MyList() {
		PurchaseOrderSearch poSearchAttributes = new PurchaseOrderSearch("ACTIVE", LocalDate.now(), "7DC299C2-7E4D-44D5-AA1C-1A459312B9AE","12Xysrr");
		PurchaseOrderPage purchaseOrderPage = new PurchaseOrderPage();
		PurchaseOrderListSegregator purchaseOrderListSegregator = new PurchaseOrderListSegregator(false, true, false,true);
		String userId = "C75F8C0D-701D-42FB-8F18-7A926F22013B";

		Page<PurchaseOrder> expectedPurchaseOrders = Mockito.mock(Page.class);
		Mockito.when(myPurchaseOrderListRepository.listMy(poSearchAttributes, purchaseOrderPage, userId))
				.thenReturn(expectedPurchaseOrders);

		Page<PurchaseOrder> result = inventoryWarehouseManagementService.getPurchaseOrders(
				poSearchAttributes, purchaseOrderPage, purchaseOrderListSegregator, userId);

		Assertions.assertEquals(expectedPurchaseOrders, result);
		Mockito.verify(myPurchaseOrderListRepository, Mockito.times(1))
				.listMy(poSearchAttributes, purchaseOrderPage, userId);
	}

	@Test
	public void testGetPurchaseOrders_ApproverList() {
		PurchaseOrderSearch poSearchAttributes = new PurchaseOrderSearch("ACTIVE", LocalDate.now(), "7DC299C2-7E4D-44D5-AA1C-1A459312B9AE","12Xysrr");
		PurchaseOrderPage purchaseOrderPage = new PurchaseOrderPage();
		PurchaseOrderListSegregator purchaseOrderListSegregator = new PurchaseOrderListSegregator(false, false, true,true);

		Page<PurchaseOrder> expectedPurchaseOrders = Mockito.mock(Page.class);
		Mockito.when(approvalPurchaseOrderListRepository.approverList(poSearchAttributes, purchaseOrderPage))
				.thenReturn(expectedPurchaseOrders);

		Page<PurchaseOrder> result = inventoryWarehouseManagementService.getPurchaseOrders(
				poSearchAttributes, purchaseOrderPage, purchaseOrderListSegregator, null);

		Assertions.assertEquals(expectedPurchaseOrders, result);
		Mockito.verify(approvalPurchaseOrderListRepository, Mockito.times(1))
				.approverList(poSearchAttributes, purchaseOrderPage);
	}

	@Test
	public void testGetPurchaseOrder_AllList() {
		String purchaseOrderId = "PO123";
		PurchaseOrderListSegregator purchaseOrderListSegregator = new PurchaseOrderListSegregator(true, false, true,true);
		PurchaseOrder expectedPurchaseOrder = new PurchaseOrder();

		when(purchaseOrderRepository.findById(purchaseOrderId))
				.thenReturn(Optional.of(expectedPurchaseOrder));

		PurchaseOrder result = inventoryWarehouseManagementService.getPurchaseOrder(purchaseOrderId, purchaseOrderListSegregator, "userId");

		assertNotNull(result);
		assertEquals(expectedPurchaseOrder, result);
		verify(purchaseOrderRepository, times(1)).findById(purchaseOrderId);
		verify(purchaseOrderRepository, never()).findByIdAndUserId(anyString(), anyString());
	}

	@Test
	public void testGetPurchaseOrder_MyList() {
		String purchaseOrderId = "PO123";
		String userId = "C75F8C0D-701D-42FB-8F18-7A926F22013B";
		PurchaseOrderListSegregator purchaseOrderListSegregator = new PurchaseOrderListSegregator(false, true, false,true);
		PurchaseOrder expectedPurchaseOrder = new PurchaseOrder();

		when(purchaseOrderRepository.findByIdAndUserId(purchaseOrderId, userId))
				.thenReturn(Optional.of(expectedPurchaseOrder));

		PurchaseOrder result = inventoryWarehouseManagementService.getPurchaseOrder(purchaseOrderId, purchaseOrderListSegregator, userId);

		assertNotNull(result);
		assertEquals(expectedPurchaseOrder, result);
		verify(purchaseOrderRepository, times(1)).findByIdAndUserId(purchaseOrderId,userId);
	}

	@Test
	public void testSubmitPurchaseOrderTest() {
		// Mock data
		String purchaseOrderId = "PO123";
		String userId = "user123";
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setPoNumber(purchaseOrderId);
		purchaseOrder.setStatusCode(PurchaseOrderStatus.DRAFT.name());
		Optional<PurchaseOrder> optionalPurchaseOrder = Optional.of(purchaseOrder);

		// Mock the behavior of the purchaseOrderRepository
		when(purchaseOrderRepository.findByIdAndStatusCode(eq(purchaseOrderId), eq(PurchaseOrderStatus.DRAFT.name())))
				.thenReturn(optionalPurchaseOrder);
		when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);

		// Call the method under test
		PurchaseOrderStatusResponse result = inventoryWarehouseManagementService.submitPurchaseOrder(purchaseOrderId, userId);

		// Verify the interactions and assertions
		verify(purchaseOrderRepository, times(1)).findByIdAndStatusCode(eq(purchaseOrderId), eq(PurchaseOrderStatus.DRAFT.name()));
		verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));

		// Assert the result
		assertEquals(purchaseOrderId, result.purchaseOrderId());
		assertEquals(PurchaseOrderStatus.PENDING_AUTHORIZATION.name(), result.status());
	}

	@Test
	public void testSubmitPurchaseOrderNotFound() {
		// Mock data
		String purchaseOrderId = "PO123";
		String userId = "user123";
		Optional<PurchaseOrder> optionalPurchaseOrder = Optional.empty();

		// Mock the behavior of the purchaseOrderRepository
		when(purchaseOrderRepository.findByIdAndStatusCode(eq(purchaseOrderId), eq(PurchaseOrderStatus.DRAFT.name())))
				.thenReturn(optionalPurchaseOrder);

		// Call the method under test and assert the exception
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			inventoryWarehouseManagementService.submitPurchaseOrder(purchaseOrderId, userId);
		});

		// Verify the interactions and assertions
		verify(purchaseOrderRepository, times(1)).findByIdAndStatusCode(eq(purchaseOrderId), eq(PurchaseOrderStatus.DRAFT.name()));
		verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));

		// Assert the exception details
		assertEquals("Purchase order not found or not in a DRAFT state", exception.getMessage());
	}

	@Test
	void testApprovePurchaseOrder() {
		// Arrange
		String purchaseOrderId = "123";
		String userId = "user1";
		ApproveOrderRequest approveOrderRequest = new ApproveOrderRequest("APPROVED");

		PurchaseOrder pendingPurchaseOrder = new PurchaseOrder();
		pendingPurchaseOrder.setPoNumber(purchaseOrderId);
		pendingPurchaseOrder.setStatusCode(PurchaseOrderStatus.PENDING_AUTHORIZATION.name());

		when(purchaseOrderRepository.findByIdAndStatusCode(purchaseOrderId, PurchaseOrderStatus.PENDING_AUTHORIZATION.name()))
				.thenReturn(Optional.of(pendingPurchaseOrder));

		// Act
		PurchaseOrderStatusResponse response = inventoryWarehouseManagementService.approvePurchaseOrder(purchaseOrderId, userId, approveOrderRequest);

		// Assert
		verify(purchaseOrderRepository).findByIdAndStatusCode(purchaseOrderId, PurchaseOrderStatus.PENDING_AUTHORIZATION.name());

		assertEquals(purchaseOrderId, response.purchaseOrderId());
		assertEquals("APPROVED", response.status());
	}

	@Test
	void testApprovePurchaseOrderNotFound() {
		String purchaseOrderId = "123";
		String userId = "user1";
		ApproveOrderRequest approveOrderRequest = new ApproveOrderRequest("APPROVED");

		when(purchaseOrderRepository.findByIdAndStatusCode(purchaseOrderId, PurchaseOrderStatus.PENDING_AUTHORIZATION.name()))
				.thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(ResourceNotFoundException.class,
				() -> inventoryWarehouseManagementService.approvePurchaseOrder(purchaseOrderId, userId, approveOrderRequest));

		verify(purchaseOrderRepository).findByIdAndStatusCode(purchaseOrderId, PurchaseOrderStatus.PENDING_AUTHORIZATION.name());
		verify(purchaseOrderRepository, never()).save(any());
	}

	@Test
	public void getPurchaseOrdersBySupplierIdTest() {
		PurchaseOrderSearch poSearchAttributes = new PurchaseOrderSearch("ACTIVE", LocalDate.now(), "7DC299C2-7E4D-44D5-AA1C-1A459312B9AE","12Xysrr");
		PurchaseOrderPage purchaseOrderPage = new PurchaseOrderPage();
		PurchaseOrderListSegregator purchaseOrderListSegregator = new PurchaseOrderListSegregator(true, false, false,true);
		String supplierId = "C75F8C0D-701D-42FB-8F18-7A926F22013B";

		// Mock the behavior of the repository method
		Page<PurchaseOrder> expectedPurchaseOrders = Mockito.mock(Page.class);
		when(purchaseOrderSupplierRepository.listBySupplierId(poSearchAttributes, purchaseOrderPage, supplierId))
				.thenReturn(expectedPurchaseOrders);

		// Call the service method
		Page<PurchaseOrder> actualPage = inventoryWarehouseManagementService.getPurchaseOrdersBySupplierId(poSearchAttributes, purchaseOrderPage, supplierId);
		// Verify the repository method was called with the correct arguments
		Mockito.verify(purchaseOrderSupplierRepository, Mockito.times(1))
				.listBySupplierId(poSearchAttributes, purchaseOrderPage, supplierId);
	}

	@Test
	public void testReceivePurchaseOrder_PurchaseOrderNotFound() {
		String poId = "123";
		String userId = "C75F8C0D-701D-42FB-8F18-7A926F22013B";
		PurchaseOrderReceiveRequest receiveRequest = createMockReceiveRequest();

		// Mock purchase order repository to return an empty Optional (purchase order not found)
		when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class, () ->
				inventoryWarehouseManagementService.receivePurchaseOrder(poId, receiveRequest, userId));

		assertEquals(HttpStatus.NOT_FOUND, exception.getErrorCode());
		assertEquals("Purchase order not found for 123", exception.getMessage());

		verify(purchaseOrderRepository, times(1)).findById(poId);
		verify(inventoryWarehouseRepository, never()).findById(anyString());
	}


	@Test
	public void testReceivePurchaseOrder_WarehouseNotFound() {
		String poId = "123";
		String userId = "C75F8C0D-701D-42FB-8F18-7A926F22013B";
		PurchaseOrderReceiveRequest receiveRequest = createMockReceiveRequest();

		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setWarehouseId("792");

		// Mock purchase order repository
		when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));


		// Mock product repository to throw NotFoundException for each received product
		when(productRepository.findById(anyString())).thenThrow(new NotFoundException(HttpStatus.NOT_FOUND, "Product 456 not found"));

		CommonException exception = assertThrows(CommonException.class, () ->
				inventoryWarehouseManagementService.receivePurchaseOrder(poId, receiveRequest, userId));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getErrorCode());
		assertEquals("Warehouse id 790 not match with purchase order", exception.getMessage());

	}

	@Test
	void testReceivePurchaseOrder_Success() {
		String poId = "PO-123";
		PurchaseOrderReceiveRequest request = createMockReceiveRequest();
		String userId = "user123";

		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setWarehouseId("790");

		// Mock purchase order repository
		when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));

		// Mock inventory warehouse repository
		when(inventoryWarehouseRepository.findById(anyString())).thenReturn(Optional.of(new Warehouse()));

		List<PurchaseOrderProductItem> productItemList =  new ArrayList<>();
		PurchaseOrderProductItem productItem = new PurchaseOrderProductItem();
		productItem.setQuantity(20);
		productItemList.add(productItem);
		// Mock purchase order product repository
		when(purchaseOrderProductRepository.findByProdId(anyString())).thenReturn(Optional.of(productItemList));

		// Mock stock receipt repository
		StockReceipt savedStockReceipt = new StockReceipt();
		savedStockReceipt.setId("96FC1B95-DE36-41AB-8AE2-8C3CA2574D98");
		when(stockReceiptRepository.save(any(StockReceipt.class))).thenReturn(savedStockReceipt);

		PurchaseOrderReceiveResponse response = inventoryWarehouseManagementService.receivePurchaseOrder(poId, request, userId);

		assertNotNull(response);
		assertEquals(poId, response.po_id());
		assertNotNull(response.stock_receipt_id());

		// Verify repository method invocations
		verify(purchaseOrderRepository, times(1)).findById(poId);
		verify(purchaseOrderProductRepository, times(2)).findByProdId(anyString());
		verify(stockReceiptRepository, times(1)).save(any(StockReceipt.class));
	}

	private PurchaseOrderReceiveRequest createMockReceiveRequest() {
		LocalDateTime receivedDate = LocalDateTime.now();

		ReceivedProducts product1 = new ReceivedProducts("456", "790", 10, true,
				List.of("SN1", "SN2"), true, LocalDateTime.of(2023, 7, 13, 0, 0));
		ReceivedProducts product2 = new ReceivedProducts("457", "790", 5, false,
				List.of(), false, null);

		List<ReceivedProducts> receivedProductsList = List.of(product1, product2);
		return new PurchaseOrderReceiveRequest(receivedDate, receivedProductsList);
	}
}
