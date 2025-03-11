package com.evidenlibrary.backend.apirest.model.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KeycloakService {

    private final RestTemplate restTemplate;
    
    @Value("${keycloak.auth-server-url:http://localhost:8082}")
    private String keycloakServerUrl;
    
    @Value("${keycloak.realm:EvidenLibrary}")
    private String realm;
    
    @Value("${keycloak.resource:eviden-library-rest-api}")
    private String clientId;
    
    @Value("${keycloak.credentials.secret:}")
    private String clientSecret;
    
    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;
    
    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    public KeycloakService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean registerUser(String firstName, String lastName, String email, String password) {
        try {
            // 1. Obtener token de administrador
            String adminToken = getAdminToken();
            if (adminToken == null) {
                return false;
            }

            // 2. Crear usuario en Keycloak
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> credentialMap = new HashMap<>();
            credentialMap.put("type", "password");
            credentialMap.put("value", password);
            credentialMap.put("temporary", false);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("firstName", firstName);
            userMap.put("lastName", lastName);
            userMap.put("email", email);
            userMap.put("username", email);
            userMap.put("enabled", true);
            userMap.put("credentials", Collections.singletonList(credentialMap));

            // Añadir rol de usuario
            Map<String, List<String>> clientRoles = new HashMap<>();
            clientRoles.put(clientId, Arrays.asList("USER"));
            userMap.put("clientRoles", clientRoles);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(userMap, headers);
            
            String userRegistrationUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users";
            ResponseEntity<Void> response = restTemplate.exchange(
                userRegistrationUrl,
                HttpMethod.POST,
                requestEntity,
                Void.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getAdminToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", "admin-cli");
            map.add("username", adminUsername);
            map.add("password", adminPassword);
            map.add("grant_type", "password");

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);
            
            String tokenUrl = keycloakServerUrl + "/realms/master/protocol/openid-connect/token";
            ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}