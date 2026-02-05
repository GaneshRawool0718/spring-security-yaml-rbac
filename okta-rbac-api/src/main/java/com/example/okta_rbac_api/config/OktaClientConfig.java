package com.example.okta_rbac_api.config;

import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OktaClientConfig {

    @Value("${okta.org.url}")
    private String orgUrl;

    @Value("${okta.client.token}")
    private String apiToken;

    @Bean
    public Client oktaClient() {
        return Clients.builder()
                .setOrgUrl(orgUrl)
                .setClientCredentials(new TokenClientCredentials(apiToken))
                .build();
    }
}
