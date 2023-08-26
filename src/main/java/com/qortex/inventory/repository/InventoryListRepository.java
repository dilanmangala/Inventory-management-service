package com.qortex.inventory.repository;
import com.qortex.inventory.dto.InventoryPage;
import com.qortex.inventory.dto.InventorySearchCriteria;
import com.qortex.inventory.dto.PurchaseOrderPage;
import com.qortex.inventory.dto.PurchaseOrderSearch;
import com.qortex.inventory.model.PurchaseOrder;
import com.qortex.inventory.model.Supplier;
import com.qortex.inventory.model.Warehouse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
@Slf4j
public class InventoryListRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public InventoryListRepository(EntityManager entityManager, CriteriaBuilder criteriaBuilder) {
        this.entityManager = entityManager;
        this.criteriaBuilder = criteriaBuilder;
    }

    public Page<Warehouse> findAllWarehouses(InventoryPage inventoryPage,
                                             InventorySearchCriteria inventorySearchCriteria){

        CriteriaQuery<Warehouse> criteriaQuery = criteriaBuilder.createQuery(Warehouse.class);
        Root<Warehouse> warehouseRoot = criteriaQuery.from(Warehouse.class);
        Predicate predicate = getPredicateForWarehouse(inventorySearchCriteria,warehouseRoot);
        criteriaQuery.where(predicate);
        setOrder(inventoryPage, criteriaQuery, warehouseRoot);

        TypedQuery<Warehouse> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(inventoryPage.getPageNumber() * inventoryPage.getPageSize());
        typedQuery.setMaxResults(inventoryPage.getPageSize());

        Pageable pageable = getPageable(inventoryPage);
        long wareHouseCount = countQuery(inventorySearchCriteria).getSingleResult();

        log.info(typedQuery.getResultList().toString());
        return new PageImpl<>(typedQuery.getResultList(), pageable, wareHouseCount);

    }

    private TypedQuery<Long> countQuery(InventorySearchCriteria inventorySearchCriteria) {
        CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Warehouse> warehouseRoot = countCriteriaQuery.from(Warehouse.class);

        countCriteriaQuery.select(criteriaBuilder.count(warehouseRoot));
        countCriteriaQuery.where(getPredicateForWarehouse(inventorySearchCriteria, warehouseRoot));

        return entityManager.createQuery(countCriteriaQuery);
    }

    private Pageable getPageable(InventoryPage inventoryPage) {
        Sort sort = Sort.by(inventoryPage.getSortDirection(), inventoryPage.getSortBy());
        return PageRequest.of(inventoryPage.getPageNumber(),inventoryPage.getPageSize(), sort);
    }

    private Predicate getPredicateForWarehouse(InventorySearchCriteria inventorySearchCriteria,
                                          Root<Warehouse> warehouseRoot){
        log.info("fetch all inventories started");
        List<Predicate> predicates = new ArrayList<>();

        checkField(inventorySearchCriteria.getWarehouseName())
                .ifPresent(name -> predicates.add(criteriaBuilder
                        .like(warehouseRoot.get("warehouseName"), "%" + name + "%")));
        checkField(inventorySearchCriteria.getWarehouseCode())
                .ifPresent(code -> predicates.add(criteriaBuilder
                        .like(warehouseRoot.get("warehouseCode"), "%" + code + "%")));
        checkField(inventorySearchCriteria.getWarehouseType())
                .ifPresent(type -> predicates.add(criteriaBuilder
                        .like(warehouseRoot.get("warehouseType"), "%" + type + "%")));

        predicates.add(criteriaBuilder
                .notEqual(warehouseRoot.get("statusCode"), "DELETED"));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Optional<String> checkField(String fieldName) {
        return Optional.ofNullable(fieldName)
                .map(String::trim)
                .filter(s -> !s.isEmpty());
    }

    private void setOrder(InventoryPage userPage,
                          CriteriaQuery<Warehouse> criteriaQuery,
                          Root<Warehouse> userRoot) {
        if(userPage.getSortDirection().equals(Sort.Direction.ASC)){
            criteriaQuery.orderBy(criteriaBuilder.asc(userRoot.get(userPage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(userRoot.get(userPage.getSortBy())));
        }
    }
}
