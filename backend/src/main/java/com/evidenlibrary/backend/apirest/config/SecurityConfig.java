package com.evidenlibrary.backend.apirest.config;
 
import java.util.Arrays;
 
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
                    .requestMatchers("/api/login", "/api/registro", "/api/principal", "/api/generos", "/api/valoraciones", "/api/valoracion", "/api/favorito", "/api/libros", "/api/libros/**", "/api/libros/mejor-valorados", "/api/libro/**", "/api/autores", "/usuario", "/api/detalles-carrito", "/api/pedido", "/api/search/**").permitAll()
 
                    // Requerir autenticación para ciertos endpoints, tanto para USER como para ADMIN
                    .requestMatchers("/api/valoraciones", "/api/detalles-carrito").hasRole("USER")
 
                    // Requerir ADMIN para endpoints específicos
                    .requestMatchers("/api/autor", "/api/carritos", "/api/carrito", "/api/pedidos", "/api/pedido",
                                     "/api/favoritos", "/api/genero", "/api/usuarios", "/api/usuario", "/api/valoracion", 
                                     "/api/libro", "/api/libros").hasRole("ADMIN")
                    
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
            	    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            	    .authenticationEntryPoint((request, response, authException) -> {
            	        // Solo redirigir a login si la ruta no es pública
            	        String requestPath = request.getRequestURI();
            	        if (!requestPath.startsWith("/api/login") &&
            	            !requestPath.startsWith("/api/registro") &&
            	            !requestPath.startsWith("/api/principal") &&
            	            !requestPath.startsWith("/api/libros") &&
            	            !requestPath.startsWith("/api/libro") &&
            	            !requestPath.startsWith("/api/autores") &&
            	            !requestPath.startsWith("/api/generos") &&
            	            !requestPath.startsWith("/api/search") &&
                            !requestPath.startsWith("/api/pedido")) {
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