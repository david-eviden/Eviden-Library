package com.evidenlibrary.backend.apirest.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class KeycloakRealmRoleConverterTest {

    private KeycloakRealmRoleConverter converter;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        converter = new KeycloakRealmRoleConverter();
        jwt = mock(Jwt.class);
    }

    @Test
    void convert_WithValidRoles_ReturnsAuthorities() {
        // Arrange
        Map<String, Object> realmAccess = new HashMap<>();
        List<String> roles = Arrays.asList("admin", "user");
        realmAccess.put("roles", roles);
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        // Act
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void convert_WithNullRealmAccess_ReturnsEmptyList() {
        // Arrange
        when(jwt.getClaim("realm_access")).thenReturn(null);

        // Act
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void convert_WithEmptyRealmAccess_ReturnsEmptyList() {
        // Arrange
        Map<String, Object> realmAccess = new HashMap<>();
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        // Act
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void convert_WithNullRoles_ReturnsEmptyList() {
        // Arrange
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", null);
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        // Act
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void convert_WithEmptyRoles_ReturnsEmptyList() {
        // Arrange
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Collections.emptyList());
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        // Act
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void convert_WhenExceptionThrown_ReturnsEmptyList() {
        // Arrange
        when(jwt.getClaim("realm_access")).thenThrow(new RuntimeException("Test Exception"));

        // Act
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }
} 