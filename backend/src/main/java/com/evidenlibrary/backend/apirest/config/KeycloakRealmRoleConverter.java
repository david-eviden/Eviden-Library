package com.evidenlibrary.backend.apirest.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Handle potential null or empty claims
        try {
            // Extract realm access from JWT claims
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            
            if (realmAccess == null || realmAccess.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Extract roles, handling potential type casting
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            
            if (roles == null || roles.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Convert roles to granted authorities
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the error or handle it appropriately
            System.err.println("Error converting JWT roles: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}