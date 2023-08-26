package com.qortex.inventory.repository;
import com.qortex.inventory.model.StockReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockReceiptRepository extends ListCrudRepository<StockReceipt, String>,
        JpaRepository<StockReceipt, String> {
}
