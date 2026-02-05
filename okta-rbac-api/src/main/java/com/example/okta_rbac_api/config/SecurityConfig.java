package com.example.okta_rbac_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Security configuration enabling method security for @PreAuthorize usage.
 * 
 * This configuration:
 * 1. Reads roles from JWT "groups" claim
 * 2. Expands each role into its associated permissions from permissions.yaml
 * 3. Grants both the role AND the expanded permissions as authorities
 * 
 * This allows using both hasRole('ADMIN') and hasAuthority('API_USER_READ')
 * in @PreAuthorize.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(SecurityPermissionsProperties.class)
public class SecurityConfig {

    private static final Logger AUTH_LOG = LoggerFactory.getLogger("AUTHORIZATION");

    private final SecurityPermissionsProperties permissionsProperties;

    public SecurityConfig(SecurityPermissionsProperties permissionsProperties) {
        this.permissionsProperties = permissionsProperties;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {
                })
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(
                        oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    /**
     * Creates a JwtAuthenticationConverter that expands roles to include
     * permissions.
     * 
     * For a JWT with groups: ["ROLE_ADMIN"], the resulting authorities will
     * include:
     * - ROLE_ADMIN (the original role)
     * - API_ADMIN_READ, API_USER_READ, API_USER_WRITE, etc. (expanded permissions
     * from YAML)
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new RoleExpandingGrantedAuthoritiesConverter());
        return jwtConverter;
    }

    /**
     * Custom converter that reads roles from JWT and expands them to include
     * all associated permissions defined in permissions.yaml.
     */
    private class RoleExpandingGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Set<GrantedAuthority> authorities = new HashSet<>();

            // Extract user info for logging
            String userId = jwt.getSubject();
            String email = jwt.getClaimAsString("email");

            // Extract roles from the "groups" claim
            List<String> groups = jwt.getClaimAsStringList("groups");
            if (groups == null || groups.isEmpty()) {
                AUTH_LOG.warn("AUTHORIZATION user={} email={} - No groups found in JWT token",
                        userId, email);
                return authorities;
            }

            Map<String, Set<String>> rolePermissions = permissionsProperties.getRoles();

            for (String group : groups) {
                // Add the role itself as an authority
                authorities.add(new SimpleGrantedAuthority(group));

                // Expand the role to include all associated permissions from YAML
                Set<String> permissions = rolePermissions.get(group);
                if (permissions != null) {
                    for (String permission : permissions) {
                        authorities.add(new SimpleGrantedAuthority(permission));
                    }
                    AUTH_LOG.debug("AUTHORIZATION user={} role={} expanded to permissions={}",
                            userId, group, permissions);
                } else {
                    AUTH_LOG.debug("AUTHORIZATION user={} role={} - No permissions mapped in YAML",
                            userId, group);
                }
            }

            // Log final authority set
            Set<String> authorityNames = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            AUTH_LOG.info("AUTHORIZATION user={} email={} roles={} grantedAuthorities={}",
                    userId, email, groups, authorityNames);

            return authorities;
        }
    }
}
