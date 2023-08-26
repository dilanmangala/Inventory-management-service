package com.qortex.inventory.common.utils;

import com.qortex.inventory.dto.RoleModulePermissionPayload;
import com.qortex.inventory.dto.SessionPayload;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoleModulePermissionProvider {

    private static final String ROLE = "ROLE";

    public RoleModulePermissionProvider() {
        super();
    }

    /**
     * GET Roles and Permissions from Redis
     *
     * @param sessionPayload
     * @return
     */

    public static Map<String, List<Map<String, Set<String>>>> getModulesAndPermissionsByRoleId(
            SessionPayload sessionPayload, ModelMapper modelMapper, RedisTemplate<String, Object> template) {
        Object employeeFromCache = template.opsForHash().get(ROLE, sessionPayload.getRoleId());
        RoleModulePermissionPayload roleModulePermissionPayload = modelMapper.map(employeeFromCache,
                RoleModulePermissionPayload.class);
        return roleModulePermissionPayload.getModulesAndPermissions();
    }

}
