package com.example.okta_rbac_api.service;

import com.example.okta_rbac_api.exception.ApplicationUserNotFoundException;
import com.example.okta_rbac_api.exception.OktaOperationException;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.application.AppUser;
import com.okta.sdk.resource.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationUserProfileService {

    private final Client oktaClient;

    @Value("${okta.app.id}")
    private String appId;

    @Value("${okta.app.idol-name-key}")
    private String idolNameKey;

    /* =======================
       PUBLIC APIs
       ======================= */

    public String getIdolName(String subject) {
        String oktaUserId = resolveOktaUserId(subject);

        log.info("[GET idolName] subject={}, resolvedUserId={}", subject, oktaUserId);

        AppUser appUser = getApplicationUser(oktaUserId);

        Object value = appUser.getProfile().get(idolNameKey);
        return value != null ? value.toString() : null;
    }

    public boolean updateIdolName(String subject, String idolName) {
        String oktaUserId = resolveOktaUserId(subject);

        log.info(
                "[UPDATE idolName] subject={}, resolvedUserId={}, value={}",
                subject,
                oktaUserId,
                idolName
        );

        AppUser appUser = getApplicationUser(oktaUserId);

        Object existing = appUser.getProfile().get(idolNameKey);
        if (Objects.equals(existing, idolName)) {
            log.info("[UPDATE idolName] Value unchanged");
            return false;
        }

        appUser.getProfile().put(idolNameKey, idolName);
        appUser.update();

        log.info("[UPDATE idolName] Update successful");
        return true;
    }

    /* =======================
       INTERNAL HELPERS
       ======================= */

    /**
     * Resolves JWT subject to Okta User ID.
     * Supports:
     * - sub = 00uXXXX (preferred)
     * - sub = email (fallback)
     */
    private String resolveOktaUserId(String subject) {
        if (subject.startsWith("00u")) {
            return subject;
        }

        try {
            User user = oktaClient.getUser(subject);
            return user.getId();
        } catch (Exception ex) {
            throw new OktaOperationException(
                    "Unable to resolve Okta user ID from subject: " + subject,
                    ex
            );
        }
    }

    private AppUser getApplicationUser(String oktaUserId) {
        try {
            return oktaClient
                    .getApplication(appId)
                    .listApplicationUsers()
                    .stream()
                    .filter(appUser -> oktaUserId.equals(appUser.getId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new ApplicationUserNotFoundException(
                                    "User is not assigned to the application"
                            )
                    );
        } catch (ApplicationUserNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new OktaOperationException(
                    "Failed to retrieve application user",
                    ex
            );
        }
    }
}
