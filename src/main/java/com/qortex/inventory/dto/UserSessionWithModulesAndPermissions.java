package com.qortex.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserSessionWithModulesAndPermissions implements Serializable {
	private static final long serialVersionUID = -7047005024550867744L;
	private boolean authenticated = false;
	private String userId;
	private String username;
	private String fullName;
	private String emailID;
	private Date expiryDate;
	private String roleId;
	private  Map<String, List<Map<String, Set<String>>>> modulesAndPermissions;
}
