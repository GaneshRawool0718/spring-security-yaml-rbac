package com.example.okta_rbac_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "security.permissions")
public class SecurityPermissionsProperties {

    private Map<String, Set<String>> roles = new HashMap<>();

    public Map<String, Set<String>> getRoles() {
        return Collections.unmodifiableMap(roles);
    }

    public void setRoles(Map<String, Set<String>> roles) {
        this.roles = roles == null ? new HashMap<>() : new HashMap<>(roles);
    }

}

