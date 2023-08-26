package com.qortex.inventory.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity()
@Table(name = "inv_warehouse_details")
public class Warehouse extends AuditableBase{
	@Id
	@GenericGenerator(name = "generator", strategy = "guid", parameters = {})
	@GeneratedValue(generator = "generator")
	@Column(name = "id", columnDefinition = "uniqueidentifier")
	private String id;
	@Column(name = "warehouse_name", nullable = false)
	private String warehouseName;
	@Column(name = "warehouse_code", nullable = false)
	private String warehouseCode;
	@Column(name = "warehouse_type", nullable = false)
	private String warehouseType;
	@Column(name = "assigned_id", nullable = false)
	private String assignedId;
	@Column(name = "status_code", nullable = false)
	private String statusCode;

	@OneToOne(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private WarehouseAddress address;

}