package com.evidenlibrary.backend.apirest.config;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.cors.CorsConfigurationSource;

@WebMvcTest(SecurityConfig.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void jwtAuthenticationConverter_ShouldNotBeNull() {
        Object converter = securityConfig.jwtAuthenticationConverter();
        assertNotNull(converter);
    }

    @Test
    void corsConfigurationSource_ShouldNotBeNull() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);
    }

    @Test
    void publicEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Test public endpoints
        mockMvc.perform(get("/api/principal"))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/api/generos"))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/api/login"))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/api/registro"))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/api/search/test"))
                .andExpect(status().isOk());
    }

    @Test
    void corsConfiguration_ShouldAllowConfiguredOrigins() throws Exception {
        mockMvc.perform(options("/api/libros")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void adminEndpoints_ShouldRequireAdminRole() throws Exception {
        // Setup admin authentication
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "password", authorities));
        
        // Test admin endpoint
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    void userEndpoints_ShouldRequireUserRole() throws Exception {
        // Setup user authentication
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", "password", authorities));
        
        // Test user endpoint
        mockMvc.perform(get("/api/valoraciones"))
                .andExpect(status().isOk());
    }
} 