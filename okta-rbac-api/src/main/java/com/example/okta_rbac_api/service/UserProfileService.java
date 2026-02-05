package com.example.okta_rbac_api.service;

import com.example.okta_rbac_api.dto.UpdateUserProfileRequest;
import com.example.okta_rbac_api.dto.UserProfileResponse;
import com.example.okta_rbac_api.exception.ApplicationUserNotFoundException;
import com.example.okta_rbac_api.exception.OktaOperationException;
import com.example.okta_rbac_api.exception.UserNotFoundException;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.application.AppUser;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final Client oktaClient;

    @Value("${okta.app.id}")
    private String appId;
    
    // Retrieves the profile of the currently authenticated user
    public UserProfileResponse getMyProfile(String subject) {
        String userId = resolveOktaUserId(subject);
        User user = getUserSafely(userId);

        String idolName = getIdolName(userId);
        List<String> roles = getUserRoles(user);

        return buildResponse(user, idolName, roles);
    }

    // Updates the profile of the currently authenticated user

    public UserProfileResponse updateMyProfile(
            String subject,
            UpdateUserProfileRequest request) {

        String userId = resolveOktaUserId(subject);
        User user = getUserSafely(userId);

        updateGlobalProfile(user, request);
        updateApplicationProfile(userId, request);

        String idolName = getIdolName(userId);
        List<String> roles = getUserRoles(user);

        return buildResponse(user, idolName, roles);
    }

    // Helper methods
    private void updateGlobalProfile(User user, UpdateUserProfileRequest request) {
        UserProfile profile = user.getProfile();

        if (hasText(request.getFirstName())) {
            profile.setFirstName(request.getFirstName());
        }
        if (hasText(request.getLastName())) {
            profile.setLastName(request.getLastName());
        }
        if (hasText(request.getNickName())) {
            profile.put("nickName", request.getNickName());
        }
        if (hasText(request.getMobilePhone())) {
            profile.setMobilePhone(request.getMobilePhone());
        }

        user.update();
        log.info("Global profile updated for userId={}", user.getId());
    }


    // Updates the application-specific profile of the user

    private void updateApplicationProfile(
            String userId,
            UpdateUserProfileRequest request) {

        if (!hasText(request.getIdolName())) {
            return;
        }

        AppUser appUser = getApplicationUser(userId);
        Object existing = appUser.getProfile().get("idolName");

        if (Objects.equals(existing, request.getIdolName())) {
            return;
        }

        appUser.getProfile().put("idolName", request.getIdolName());
        appUser.update();

        log.info("Application profile updated for userId={}", userId);
    }

    // Retrieves the "idolName" from the application-specific profile
    private String getIdolName(String userId) {
        try {
            AppUser appUser = getApplicationUser(userId);
            Object value = appUser.getProfile().get("idolName");
            return value != null ? value.toString() : null;
        } catch (ApplicationUserNotFoundException ex) {
            return null;
        }
    }

    // Retrieves the roles assigned to the user based on their group memberships

    private List<String> getUserRoles(User user) {
        List<String> roles = user.listGroups()
                .stream()
                .map(group -> group.getProfile().getName())
                .filter(name -> name.startsWith("ROLE_"))
                .distinct()
                .toList();

        log.info("Resolved roles for userId={}: {}", user.getId(), roles);
        return roles;
    }

    // Builds the UserProfileResponse DTO
    private UserProfileResponse buildResponse(
            User user,
            String idolName,
            List<String> roles) {

        UserProfile profile = user.getProfile();

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(profile.getEmail())
                .login(profile.getLogin())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .nickName((String) profile.get("nickName"))
                .mobilePhone(profile.getMobilePhone())
                .idolName(idolName)
                .status(user.getStatus().toString())
                .roles(roles)
                .build();
    }

    // Retrieves the AppUser for the given userId within the specified application
    private AppUser getApplicationUser(String userId) {
        return oktaClient
                .getApplication(appId)
                .listApplicationUsers()
                .stream()
                .filter(appUser -> userId.equals(appUser.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new ApplicationUserNotFoundException(
                                "User is not assigned to the application"
                        ));
    }

    // Resolves the Okta user ID from the provided subject  
    private String resolveOktaUserId(String subject) {
        if (subject.startsWith("00u")) {
            return subject;
        }
        try {
            return oktaClient.getUser(subject).getId();
        } catch (Exception ex) {
            throw new OktaOperationException(
                    "Unable to resolve Okta user ID from subject", ex);
        }
    }

    // Safely retrieves the User object, throwing UserNotFoundException if not found
    private User getUserSafely(String userId) {
        try {
            return oktaClient.getUser(userId);
        } catch (Exception ex) {
            throw new UserNotFoundException("User not found");
        }
    }

    // Checks if a string has text (not null and not blank)
    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
