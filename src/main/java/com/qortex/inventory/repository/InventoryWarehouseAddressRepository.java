package com.qortex.inventory.repository;

import com.qortex.inventory.model.WarehouseAddress;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryWarehouseAddressRepository
		extends PagingAndSortingRepository<WarehouseAddress, String>, ListCrudRepository<WarehouseAddress, String> {

	@Transactional
	@Query(value = "UPDATE inv_warehouse_address SET status_code = :deleted, deleted_on = :localDateTime, deleted_by=:userId WHERE inv_warehouse_id IN :warehouseIds ", nativeQuery = true)
	@Modifying
	int deleteWarehouseAddress(@Param("deleted") String deleted, @Param("localDateTime") LocalDateTime localDateTime,
			@Param("userId") String userId, @Param("warehouseIds") List<String> warehouseIds);

}
