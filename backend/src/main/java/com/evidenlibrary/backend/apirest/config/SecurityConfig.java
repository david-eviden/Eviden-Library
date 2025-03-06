package com.evidenlibrary.backend.apirest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
            // Permitir acceso público a endpoints específicos
            .requestMatchers("/api/login", "/api/principal", "/api/libros/**").permitAll()

             // Requerir autenticación para otros endpoints
             .requestMatchers("/api/autores").hasRole("ADMIN")
             .requestMatchers("/api/autor").hasRole("ADMIN")
             .requestMatchers("/api/carritos").hasRole("ADMIN")
             .requestMatchers("/api/carrito").hasRole("ADMIN")
             .requestMatchers("/api/pedidos").hasRole("ADMIN")
             .requestMatchers("/api/pedido").hasRole("ADMIN")
             .requestMatchers("/api/favoritos").hasRole("ADMIN")
             .requestMatchers("/api/favorito").hasRole("ADMIN")
             .requestMatchers("/api/generos").hasRole("ADMIN")
             .requestMatchers("/api/genero").hasRole("ADMIN")
             .requestMatchers("/api/usuarios").hasRole("ADMIN")
             .requestMatchers("/api/usuario").hasRole("ADMIN")
             .requestMatchers("/api/valoraciones").hasRole("ADMIN")
             .requestMatchers("/api/valoracion").hasRole("ADMIN")
             .requestMatchers("/api/libro").hasRole("ADMIN")
             
             .requestMatchers("/api/libros").permitAll()
             .requestMatchers("/usuario/{id}").permitAll()
              
             .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
            	    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            	    .authenticationEntryPoint((request, response, authException) -> {
            	        // Solo redirigir a login si la ruta no es pública
            	        String requestPath = request.getRequestURI();
            	        if (!requestPath.startsWith("/api/login") && 
            	            !requestPath.startsWith("/api/principal") && 
            	            !requestPath.startsWith("/api/libros")) {
            	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	        } else {
            	            // Para rutas públicas, permitir acceso incluso sin token
            	            response.setStatus(HttpServletResponse.SC_OK);
            	        }
            	    })
            );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        
        return jwtConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}