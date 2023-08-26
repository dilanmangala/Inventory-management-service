package com.qortex.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "INV_STOCK_RECEIPT")
@Data
public class StockReceipt extends AuditableBase {
    @Id
    @GenericGenerator(name = "generator", strategy = "guid")
    @GeneratedValue(generator = "generator")
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private String id;

    @Column(name = "po_id", nullable = false)
    private String purchaseOrderId;

    @Column(name = "received_date")
    private LocalDateTime poReceivedDate;

    @Column(name = "status_code")
    private String statusCode;

    @OneToMany(mappedBy = "stockReceipt")
    private List<StockReceiptItem> receiptItems;

}
