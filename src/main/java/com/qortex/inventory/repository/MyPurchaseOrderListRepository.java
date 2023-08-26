package com.qortex.inventory.repository;

import com.qortex.inventory.dto.PurchaseOrderPage;
import com.qortex.inventory.dto.PurchaseOrderSearch;
import com.qortex.inventory.model.PurchaseOrder;
import com.qortex.inventory.model.Supplier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class MyPurchaseOrderListRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    private final TenantSupplierRepository tenantSupplierRepository;

    @Autowired
    public MyPurchaseOrderListRepository(EntityManager entityManager,
                                       CriteriaBuilder criteriaBuilder,
                                       TenantSupplierRepository tenantSupplierRepository) {
        this.entityManager = entityManager;
        this.criteriaBuilder = criteriaBuilder;
        this.tenantSupplierRepository = tenantSupplierRepository;
    }

    public Page<PurchaseOrder> listMy(PurchaseOrderSearch purchaseOrderSearch,
                                                      PurchaseOrderPage purchaseOrderPage, String userId) {
        CriteriaQuery<PurchaseOrder> criteriaQuery = criteriaBuilder.createQuery(PurchaseOrder.class);
        Root<PurchaseOrder> purchaseOrderRoot = criteriaQuery.from(PurchaseOrder.class);

        Join<PurchaseOrder, Supplier> supplierJoin = purchaseOrderRoot.join("supplierId");

        Predicate predicate = getPredicatesForPurchaseOrders(purchaseOrderSearch, purchaseOrderRoot, supplierJoin, userId);
        criteriaQuery.where(predicate);

        setOrder(purchaseOrderPage, criteriaQuery, purchaseOrderRoot);

        long count = getPurchaseOrderCount(purchaseOrderSearch, userId);
        Pageable pageable =  getPageable(purchaseOrderPage);

        TypedQuery<PurchaseOrder> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(purchaseOrderPage.getPageNumber() * purchaseOrderPage.getPageSize());
        typedQuery.setMaxResults(purchaseOrderPage.getPageSize());

        List<PurchaseOrder> purchaseOrders = typedQuery.getResultList();
        if (!purchaseOrders.isEmpty()){
            List<PurchaseOrder> purchaseOrdersWithCode =  purchaseOrders.stream()
                    .peek(purchaseOrder -> {
                        tenantSupplierRepository
                                .getSupplierCodeBySupplierId(purchaseOrder.getSupplierId().getId())
                                .ifPresentOrElse(purchaseOrder::setSupplierCode,() -> purchaseOrder.setSupplierCode(null));

                    }).toList();

            return new PageImpl<>(purchaseOrdersWithCode,pageable, count);
        }
        return new PageImpl<>(typedQuery.getResultList(), pageable, count);
    }


    private Predicate getPredicatesForPurchaseOrders(PurchaseOrderSearch purchaseOrderSearch,
                                                     Root<PurchaseOrder> purchaseOrderRoot,
                                                     Join<PurchaseOrder, Supplier> supplierJoin,
                                                     String userId){
        log.info("fetch all purchase orders predicate");
        List<Predicate> predicates = new ArrayList<>();

        checkField(purchaseOrderSearch.getPoNumber())
                .ifPresent(poNumber -> predicates.add(criteriaBuilder
                        .like(purchaseOrderRoot.get("poNumber"), "%" + poNumber + "%")));

        checkField(purchaseOrderSearch.getStatusCode())
                .ifPresent(statusCode -> predicates.add(criteriaBuilder
                        .equal(
                                criteriaBuilder.lower(purchaseOrderRoot.get("statusCode")),
                                statusCode.toLowerCase())));

        if (purchaseOrderSearch.getIssueDate() != null) {
            LocalDate issueDate = purchaseOrderSearch.getIssueDate();
            predicates.add(
                    criteriaBuilder.equal(
                            purchaseOrderRoot.get("poIssueDate"),issueDate));
        }

        if(userId != null && !userId.isEmpty()){
          predicates.add(
                  criteriaBuilder.equal(
                          purchaseOrderRoot.get("userId"),userId)
          );
        }

        checkField(purchaseOrderSearch.getSupplier())
                .ifPresent(supplierName -> predicates.add(criteriaBuilder
                        .like(supplierJoin.get("supplierNameEn"), "%" + supplierName + "%")));

        predicates.add(criteriaBuilder.equal(purchaseOrderRoot.get("supplierId"), supplierJoin));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Optional<String> checkField(String fieldName) {
        return Optional.ofNullable(fieldName)
                .map(String::trim)
                .filter(s -> !s.isEmpty());
    }


    private void setOrder(PurchaseOrderPage purchaseOrderPage, CriteriaQuery<PurchaseOrder> criteriaQuery,
                          Root<PurchaseOrder> purchaseOrderRoot) {
        if(purchaseOrderPage.getSortDirection().equals(Sort.Direction.ASC)){
            criteriaQuery.orderBy(criteriaBuilder.asc(purchaseOrderRoot.get(purchaseOrderPage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(purchaseOrderRoot.get(purchaseOrderPage.getSortBy())));
        }
    }

    private Pageable getPageable(PurchaseOrderPage enterprisePage) {
        Sort sort = Sort.by(enterprisePage.getSortDirection(), enterprisePage.getSortBy());
        return PageRequest.of(enterprisePage.getPageNumber(), enterprisePage.getPageSize(), sort);
    }

    public long getPurchaseOrderCount(PurchaseOrderSearch purchaseOrderSearch, String userId) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<PurchaseOrder> countRoot = countQuery.from(PurchaseOrder.class);
        Join<PurchaseOrder, Supplier> supplierJoin = countRoot.join("supplierId", JoinType.INNER);
        Predicate predicate = getPredicatesForPurchaseOrders(purchaseOrderSearch, countRoot, supplierJoin, userId);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        TypedQuery<Long> typedQuery = entityManager.createQuery(countQuery);
        return typedQuery.getSingleResult();
    }
}
