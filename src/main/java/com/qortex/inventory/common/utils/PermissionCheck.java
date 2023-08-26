package com.qortex.inventory.common.utils;

import com.qortex.inventory.dto.PurchaseOrderListSegregator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PermissionCheck {
	boolean check(Map<String, List<Map<String, Set<String>>>> modulesAndPermissionsByRoleId,
				  String Module,
				  List<Map<String, Set<String>>> modulePermissionList);

	boolean checkUpdate(Map<String, List<Map<String, Set<String>>>> modulesAndPermissions, String module,
			List<Map<String, Set<String>>> modulesAndPermsList, 
			List<Map<String, Set<String>>> modulesAndPermsListTwo);

	PurchaseOrderListSegregator checkListSegregate(Map<String, List<Map<String, Set<String>>>> modulesAndPermissionsByRoleId,
												   String Module,
												   List<Map<String, Set<String>>> modulePermissionList);


}
