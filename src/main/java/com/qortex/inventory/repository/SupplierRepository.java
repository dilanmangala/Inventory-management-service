package com.qortex.inventory.repository;


import com.qortex.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends ListCrudRepository<Supplier, String>,
        JpaRepository<Supplier, String> {
}
