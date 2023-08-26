package com.qortex.inventory.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Entity
@Table(name = "inv_po_product_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderProductItem extends AuditableBase{

    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private String id;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "tax")
    private BigDecimal tax;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "rate")
    private Integer rate;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "prod_id")
    private String prodId;

    @Column(name = "prod_name")
    private String productName;

    @Column(name = "prod_description")
    private String productDescription;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    @JsonBackReference
    private PurchaseOrder purchaseOrder;
}
