package com.qortex.inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@MappedSuperclass
public class AuditableBase {

	@Column(name = "created_on", nullable = false)
	@JsonIgnore
	private LocalDateTime createdOn;

	@Column(name = "created_by", nullable = false)
	@JsonIgnore
	private String createdBy;

	@Column(name = "updated_on")
	@JsonIgnore
	private LocalDateTime updatedOn;

	@Column(name = "updated_by")
	@JsonIgnore
	private String updatedBy;

	@Column(name = "deleted_on")
	@JsonIgnore
	private LocalDateTime deletedOn;

	@Column(name = "deleted_by")
	@JsonIgnore
	private String deletedBy;

}
