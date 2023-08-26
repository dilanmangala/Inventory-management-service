package com.qortex.inventory.repository;


import com.qortex.inventory.model.TenantSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TenantSupplierRepository extends ListCrudRepository<TenantSupplier, String>,
        JpaRepository<TenantSupplier, String> {

    @Query(value = "SELECT ts.supplier_code FROM sup_tenant_supplier ts WHERE ts.supplier_id =:id", nativeQuery = true)
    Optional<String> getSupplierCodeBySupplierId(@Param("id") String id);
}

