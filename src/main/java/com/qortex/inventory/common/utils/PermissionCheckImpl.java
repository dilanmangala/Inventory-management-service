package com.qortex.inventory.common.utils;

import com.qortex.inventory.dto.PurchaseOrderListSegregator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class PermissionCheckImpl implements PermissionCheck {

	@Override
	public boolean check(Map<String, List<Map<String,
			Set<String>>>> modulesAndPermissionsByRoleId,
						 String Module, List<Map<String, Set<String>>> modulePermissionList) {
		log.info("module permission for checkOne start");
		List<Map<String, Set<String>>> modulePermissions =  modulesAndPermissionsByRoleId.get(Module);
		if(modulePermissions == null || modulePermissions.isEmpty()){
			return false;
		}
		return modulePermissions.contains(modulePermissionList.get(0));
	}

	@Override
	public PurchaseOrderListSegregator checkListSegregate(Map<String, List<Map<String, Set<String>>>> modulesAndPermissionsByRoleId,
														  String Module,
														  List<Map<String, Set<String>>> modulePermissionList) {

		List<Map<String, Set<String>>> modulePermissions =  modulesAndPermissionsByRoleId.get(Module);
		if (modulePermissions == null || modulePermissions.isEmpty()) {
			return new PurchaseOrderListSegregator(false, false, false, false);
		} else {
			boolean condition1 = modulePermissions.contains(modulePermissionList.get(0));
			boolean condition2 = modulePermissions.contains(modulePermissionList.get(1));
			boolean condition3 = modulePermissions.contains(modulePermissionList.get(2));
			return new PurchaseOrderListSegregator(condition1, condition2, condition3, true);
		}
	}

	@Override
	public boolean checkUpdate(Map<String, List<Map<String, Set<String>>>> modulesAndPermissions, String module,
							   List<Map<String, Set<String>>> modulesAndPermsList, List<Map<String, Set<String>>> modulesAndPermsListTwo) {

		List<Map<String, Set<String>>> modulePermissions = modulesAndPermissions.get(module);
		if (modulePermissions == null || modulePermissions.isEmpty()) {
			return false;
		}
		return modulePermissions.contains(modulesAndPermsList.get(0))
				|| modulePermissions.contains(modulesAndPermsListTwo.get(0));
	}

}
