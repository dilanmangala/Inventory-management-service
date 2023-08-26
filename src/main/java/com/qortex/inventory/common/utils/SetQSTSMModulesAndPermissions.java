package com.qortex.inventory.common.utils;

import java.util.*;

public class SetQSTSMModulesAndPermissions {
    public static List<Map<String, Set<String>>> setModulesAndPermissionsSetOne(String permission, String module) {
        Set<String> permissionsInitiate = new HashSet<>();
        Map<String, Set<String>> modulesAndPermsOne = new HashMap<>();
        List<Map<String, Set<String>>> modulesAndPermsList = new ArrayList<>();
        permissionsInitiate.add(permission);
        modulesAndPermsOne.put(module, permissionsInitiate);
        modulesAndPermsList.add(modulesAndPermsOne);
        return modulesAndPermsList;
    }

    public static List<Map<String, Set<String>>> setModulesAndPermissionsSetOne(String permission, String permissionTwo,
                                                                                String module, String moduleTwo, String moduleThree) {
        return List.of(
                Map.of(module, Set.of(permission)),
                Map.of(moduleTwo, Set.of(permission)),
                Map.of(moduleThree, Set.of(permissionTwo))
        );
    }
    public static List<Map<String, Set<String>>> setModulesAndPermissions(String permission, String module, boolean useFirstMap) {

        {
            Set<String> permissionsInitiate = new HashSet<>();
            Map<String, Set<String>> modulesAndPerms = useFirstMap ? new HashMap<>() : new LinkedHashMap<>();
            List<Map<String, Set<String>>> modulesAndPermsList = new ArrayList<>();
            permissionsInitiate.add(permission);
            modulesAndPerms.put(module, permissionsInitiate);
            modulesAndPermsList.add(modulesAndPerms);
            return modulesAndPermsList;
        }
    }
}
