package com.qortex.inventory.repository;

import com.qortex.inventory.model.StockReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockReceiptItemRepository extends ListCrudRepository<StockReceiptItem, String>,
        JpaRepository<StockReceiptItem, String> {
}
