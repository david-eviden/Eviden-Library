package com.evidenlibrary.backend.apirest.model.service;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

class KeycloakServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KeycloakService keycloakService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inyectar valores de configuración para los tests
        ReflectionTestUtils.setField(keycloakService, "keycloakServerUrl", "http://localhost:8082");
        ReflectionTestUtils.setField(keycloakService, "realm", "EvidenLibrary");
        ReflectionTestUtils.setField(keycloakService, "clientId", "eviden-library-rest-api");
        ReflectionTestUtils.setField(keycloakService, "adminUsername", "admin");
        ReflectionTestUtils.setField(keycloakService, "adminPassword", "admin");
        
        // Reemplazar el RestTemplate interno con nuestro mock
        ReflectionTestUtils.setField(keycloakService, "restTemplate", restTemplate);
    }

    @Test
    void registerUser_Success() {
        // Mock para obtener admin token
        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", "admin-token-123");
        
        @SuppressWarnings("rawtypes")
		ResponseEntity<Map> tokenResponseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                matches(".*/realms/master/protocol/openid-connect/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(tokenResponseEntity);
        
        // Mock para registrar usuario
        ResponseEntity<Void> registrationResponseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        when(restTemplate.exchange(
                matches(".*/admin/realms/EvidenLibrary/users"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(registrationResponseEntity);
        
        // Test
        boolean result = keycloakService.registerUser("Juan", "Pérez", "juan.perez@example.com", "password123");
        
        // Verify
        assertTrue(result);
        verify(restTemplate, times(1)).exchange(
                matches(".*/realms/master/protocol/openid-connect/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
        verify(restTemplate, times(1)).exchange(
                matches(".*/admin/realms/EvidenLibrary/users"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }
    
    @Test
    void registerUser_AdminTokenFailure() {
        // Mock para obtener admin token que falla
        when(restTemplate.exchange(
                matches(".*/realms/master/protocol/openid-connect/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        
        // Test
        boolean result = keycloakService.registerUser("Juan", "Pérez", "juan.perez@example.com", "password123");
        
        // Verify
        assertFalse(result);
        verify(restTemplate, times(1)).exchange(
                matches(".*/realms/master/protocol/openid-connect/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
        verify(restTemplate, never()).exchange(
                matches(".*/admin/realms/EvidenLibrary/users"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }
    
    @SuppressWarnings({ "rawtypes", "null" })
	@Test
    void registerUser_NullTokenResponse() {
        // Mock para obtener admin token que devuelve nulo
        ResponseEntity<Map> tokenResponseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                matches(".*/realms/master/protocol/openid-connect/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(tokenResponseEntity);
        
        // Test
        boolean result = keycloakService.registerUser("Juan", "Pérez", "juan.perez@example.com", "password123");
        
        // Verify
        assertFalse(result);
    }
    
    @Test
    void registerUser_AdminTokenException() {
        // Mock para obtener admin token que lanza excepción
        when(restTemplate.exchange(
                matches(".*/realms/master/protocol/openid-connect/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException("Test exception"));
        
        // Test
        boolean result = keycloakService.registerUser("Juan", "Pérez", "juan.perez@example.com", "password123");
        
        // Verify
        assertFalse(result);
    }
    
    @Test
    void registerUser_UserRegistrationFailure() {
        // Mock para obtener admin token
        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", "admin-token-123");
        
        @SuppressWarnings("rawtypes")
		ResponseEntity<Map> tokenResponseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                matches(".*/realms/master/protocol/openid-connect/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(tokenResponseEntity);
        
        // Mock para registrar usuario que falla
        ResponseEntity<Void> registrationResponseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(
                matches(".*/admin/realms/EvidenLibrary/users"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(registrationResponseEntity);
        
        // Test
        boolean result = keycloakService.registerUser("Juan", "Pérez", "juan.perez@example.com", "password123");
        
        // Verify
        assertFalse(result);
    }
    
    @Test
    void registerUser_UserRegistrationException() {
        // Mock para obtener admin token
        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", "admin-token-123");
        
        @SuppressWarnings("rawtypes")
		ResponseEntity<Map> tokenResponseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                matches(".*/realms/master/protocol/openid-connect/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(tokenResponseEntity);
        
        // Mock para registrar usuario que lanza excepción
        when(restTemplate.exchange(
                matches(".*/admin/realms/EvidenLibrary/users"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenThrow(new RuntimeException("Test exception"));
        
        // Test
        boolean result = keycloakService.registerUser("Juan", "Pérez", "juan.perez@example.com", "password123");
        
        // Verify
        assertFalse(result);
    }
} 