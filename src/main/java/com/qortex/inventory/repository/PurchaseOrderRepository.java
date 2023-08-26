package com.qortex.inventory.repository;

import com.qortex.inventory.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends ListCrudRepository<PurchaseOrder, String>,
        JpaRepository<PurchaseOrder, String> {

    Optional<PurchaseOrder> findByIdAndUserId(String poNumber, String userId);

    Optional<PurchaseOrder> findByIdAndStatusCode(String poNumber, String statusCode);
}
