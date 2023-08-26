package com.qortex.inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "inv_purchase_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder extends AuditableBase{

    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private String id;

    @Column(name = "entity_id")
    @JsonIgnore
    private String entityId;

    @Column(name = "po_number")
    private String poNumber;

    @OneToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplierId;

    @Column(name = "po_issue_date")
    private LocalDate poIssueDate;

    @Column(name = "po_received_date")
    private LocalDate poReceivedDate;

    @Column(name = "user_id")
    @JsonIgnore
    private String userId;

    @Column(name = "warehouse_id")
    @JsonIgnore
    private String warehouseId;

    @Column(name = "subtotal_amount")
    @JsonIgnore
    private BigDecimal subtotalAmount;

    @Column(name = "shipping_amount")
    @JsonIgnore
    private BigDecimal shippingAmount;

    @Column(name = "tax")
    @JsonIgnore
    private BigDecimal tax;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "note")
    @JsonIgnore
    private String note;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "quotation")
    private String quotationNumber;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<PurchaseOrderProductItem> purchaseOrderProductItems;

    @Transient
    private String supplierCode;

}
