package com.example.okta_rbac_api.controller;

import com.example.okta_rbac_api.dto.UpdateIdolNameRequest;
import com.example.okta_rbac_api.service.ApplicationUserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Application User Profile API Controller.
 * 
 * Class-level authorization: All endpoints require API_APP_USER_READ or
 * API_APP_USER_WRITE permission.
 * Users with ROLE_USER or ROLE_ADMIN automatically have these permissions (via
 * permissions.yaml).
 */
@PreAuthorize("hasAnyAuthority('API_APP_USER_READ', 'API_APP_USER_WRITE')")
@RestController
@RequestMapping("/api/app-user")
@RequiredArgsConstructor
public class ApplicationUserProfileController {

        private final ApplicationUserProfileService service;

        /**
         * Get idol name - uses class-level authorization.
         */
        @GetMapping("/me/idol-name")
        public ResponseEntity<Map<String, String>> getIdolName(
                        @AuthenticationPrincipal Jwt jwt) {
                String idolName = service.getIdolName(jwt.getSubject());
                return ResponseEntity.ok(Map.of("idolName", idolName));
        }

        /**
         * Update idol name - requires API_APP_USER_WRITE permission specifically.
         */
        @PreAuthorize("hasAuthority('API_APP_USER_WRITE')")
        @PutMapping("/me/idol-name")
        public ResponseEntity<Map<String, String>> updateIdolName(
                        @AuthenticationPrincipal Jwt jwt,
                        @Valid @RequestBody UpdateIdolNameRequest request) {
                boolean updated = service.updateIdolName(jwt.getSubject(), request.getIdolName());

                return ResponseEntity.ok(
                                Map.of(
                                                "message",
                                                updated
                                                                ? "Idol name updated successfully"
                                                                : "Idol name already up to date"));
        }
}
