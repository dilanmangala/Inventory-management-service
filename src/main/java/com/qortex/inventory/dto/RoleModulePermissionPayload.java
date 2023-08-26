package com.qortex.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RoleModulePermissionPayload {
	private String roleId;
	Map<String, List<Map<String, Set<String>>>> modulesAndPermissions;
}
