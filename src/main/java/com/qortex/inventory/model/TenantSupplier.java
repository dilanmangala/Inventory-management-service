package com.qortex.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "sup_tenant_supplier")
public class TenantSupplier {
    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private String id;

    @OneToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplierId;
    @Column(name = "supplier_code")
    private String supplierCode;
}

