package com.qortex.inventory.repository;

import com.qortex.inventory.model.PurchaseOrderProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderProductRepository extends ListCrudRepository<PurchaseOrderProductItem, String>,
        JpaRepository<PurchaseOrderProductItem, String> {

      Optional<PurchaseOrderProductItem> findByIdAndStatusCodeNot(String id, String statusCode);

      Optional<List<PurchaseOrderProductItem>> findByProdId(String id);

}
