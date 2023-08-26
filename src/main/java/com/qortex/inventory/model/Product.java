package com.qortex.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "prd_product")
@Data
public class Product extends AuditableBase{

    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private String id;
    @Column(name = "prod_type",nullable = false)
    private String prodType;
    @Column(name = "prod_name",nullable = false)
    private String prodName;
    @Column(name = "prod_descrip",nullable = false)
    private String prodDescrip;
    @Column(name = "prod_name_ar")
    private String prodNameAr;
    @Column(name = "prod_descrip_ar")
    private String prodDescripAr;
    @Column(name = "prod_code")
    private String prodCode;
    @Column(name = "prod_category",nullable = false)
    private String prodCategory;
    @Column(name = "prod_class")
    private String prodClass;
    @Column(name = "barcode")
    private String barcode;
    @Column(name = "vat_type",nullable = false)
    private String vatType;
    @Column(name = "vat_rate",nullable = false)
    private String vatRate;
    @Column(name = "product_logo")
    private String productLogo;
    @Column(name = "notes")
    private String notes;
    @Column(name = "status_code",nullable = false)
    private String statusCode;
}
