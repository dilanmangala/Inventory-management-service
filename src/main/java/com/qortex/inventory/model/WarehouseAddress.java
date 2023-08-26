package com.qortex.inventory.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "inv_warehouse_address",schema = "dbo")
public class WarehouseAddress extends AuditableBase {

	@Id
	@GenericGenerator(name = "generator", strategy = "guid", parameters = {})
	@GeneratedValue(generator = "generator")
	@Column(name = "id", columnDefinition = "uniqueidentifier")
	private String id;
	@Column(name = "door_no")
	private String doorName;
	@Column(name = "building_name")
	private String buildingName;
	@Column(name = "street")
	private String street;
	@Column(name = "city")
	private String city;
	@Column(name = "state")
	private String state;
	@Column(name = "country")
	private String country;
	@Column(name = "zip_code")
	private String zipCode;
	@Column(name = "status_code")
	private String statusCode;

	@OneToOne
	@JoinColumn(name = "inv_warehouse_id")
	@JsonIgnoreProperties("address")
	@JsonIgnore
	private Warehouse warehouse;
}