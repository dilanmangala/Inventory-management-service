package com.qortex.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "INV_STOCK_RECEIPT_ITEM")
@Data
public class StockReceiptItem extends AuditableBase{

    @Id
    @GenericGenerator(name = "generator", strategy = "guid")
    @GeneratedValue(generator = "generator")
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private String id;

    @ManyToOne
    @JoinColumn(name = "stock_receipt_id",nullable = false)
    private StockReceipt stockReceipt;

    @Column(name = "prod_id", nullable = false)
    private String productId;

    @Column(name = "warehouse_id", nullable = false)
    private String warehouseId;

    @Column(name = "quantity_received")
    private Integer quantityReceived;

    @Column(name = "check_quantity_flag")
    private Boolean quantityFlag;

    @Column(name = "serial_number_list")
    private List<String> serialNumbers;

    @Column(name = "check_serial_number_flag")
    private Boolean serialNumberFlag;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "received_by")
    private String receivedBy;

    @Column(name = "status_code")
    private String statusCode;

}
