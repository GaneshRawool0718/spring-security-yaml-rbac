package com.example.okta_rbac_api.controller;

import com.example.okta_rbac_api.dto.UpdateUserProfileRequest;
import com.example.okta_rbac_api.dto.UserProfileResponse;
import com.example.okta_rbac_api.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * User Profile API Controller.
 * 
 * Class-level authorization: All endpoints require API_USER_READ or
 * API_USER_WRITE permission.
 * Users with ROLE_USER or ROLE_ADMIN automatically have these permissions (via
 * permissions.yaml).
 */
@PreAuthorize("hasAnyAuthority('API_USER_READ', 'API_USER_WRITE')")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Get current user profile.
     * 
     * Uses class-level authorization - requires API_USER_READ or API_USER_WRITE.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {
        UserProfileResponse response = userProfileService.getMyProfile(jwt.getSubject());

        return ResponseEntity.ok(response);
    }

    /**
     * Update current user profile.
     * 
     * Requires API_USER_WRITE permission specifically for write operations.
     */
    @PreAuthorize("hasAuthority('API_USER_WRITE')")
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserProfileRequest request) {

        UserProfileResponse response = userProfileService.updateMyProfile(jwt.getSubject(), request);

        return ResponseEntity.ok(response);
    }
}
