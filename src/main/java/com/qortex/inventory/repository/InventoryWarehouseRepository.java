package com.qortex.inventory.repository;

import com.qortex.inventory.model.Warehouse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryWarehouseRepository
        extends PagingAndSortingRepository<Warehouse, String>, ListCrudRepository<Warehouse, String> {

    @Transactional
    @Query(value = "UPDATE inv_warehouse_details SET status_code =:deleted , deleted_on = :localDateTime, deleted_by=:userId WHERE id IN :warehouseIds ", nativeQuery = true)
    @Modifying
    int deleteWarehouse(@Param("deleted") String deleted, @Param("localDateTime") LocalDateTime localDateTime,
                        @Param("userId") String userId, @Param("warehouseIds") List<String> warehouseIds);

    Optional<Warehouse> findByWarehouseNameAndStatusCodeNot(String warehouseName, String statusCode);

    Optional<Warehouse> findByIdAndStatusCodeNot(String id, String statusCode);

    @Query(value = "SELECT first_name FROM iam_user WHERE id=:id", nativeQuery = true)
    String getUserAssignName(@Param("id") String id);

    @Query(value = "SELECT name_en FROM cor_enterprise_temp WHERE id=:id", nativeQuery = true)
    String getUserAssignEnterpriseName(@Param("id") String id);
}
