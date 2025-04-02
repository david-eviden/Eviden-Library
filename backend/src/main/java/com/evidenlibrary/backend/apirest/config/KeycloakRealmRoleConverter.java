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
        // Manejas peticiones vacias o nulas
        try {
            // Extraer acceso al Realm con JWT
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            
            if (realmAccess == null || realmAccess.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Extraer roles, manejar casting
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            
            if (roles == null || roles.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Convertir roles a authorities
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            // Error
            System.err.println("Error converting JWT roles: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}