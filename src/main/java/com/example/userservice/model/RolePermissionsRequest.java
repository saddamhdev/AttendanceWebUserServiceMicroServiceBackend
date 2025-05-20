package com.example.userservice.model;

import java.util.Map;

// Class to map the request body
public  class RolePermissionsRequest {
    private String roleName;
    private Map<String, Boolean> permissions;

    // Getters and setters
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }
}