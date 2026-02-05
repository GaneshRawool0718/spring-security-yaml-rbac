package com.example.okta_rbac_api.controller;

import com.example.okta_rbac_api.dto.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API Test Controller demonstrating authorization with Spring Security
 * annotations.
 * 
 * Uses native @PreAuthorize with hasRole() and hasAuthority() backed by
 * permissions.yaml.
 * Roles are automatically expanded to include their permissions at
 * authentication time.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * User endpoint - accessible by users with ROLE_USER or ROLE_ADMIN.
     * 
     * Since roles are expanded to permissions, users with these roles
     * automatically have API_USER_READ authority as well.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user")
    public ApiResponse user() {
        return new ApiResponse("USER access granted");
    }

    /**
     * Admin endpoint - accessible only by users with ROLE_ADMIN.
     * 
     * Alternative: @PreAuthorize("hasAuthority('API_ADMIN_READ')")
     * would also work since ROLE_ADMIN includes API_ADMIN_READ permission.
     */
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('API_ADMIN_READ')")
    @GetMapping("/admin")
    public ApiResponse admin() {
        return new ApiResponse("ADMIN access granted");
    }
}
