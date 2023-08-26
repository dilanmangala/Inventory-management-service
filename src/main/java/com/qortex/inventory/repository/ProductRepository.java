package com.qortex.inventory.repository;

import com.qortex.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ListCrudRepository<Product, String>,
        JpaRepository<Product, String> {
}
