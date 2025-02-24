package com.evidenlibrary.backend.apirest.config;

/*import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.web.SecurityFilterChain;
import org.keycloak.adapters.springsecurity.KeycloakSecurityConfig;*/
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

@EnableWebSecurity
@KeycloakConfiguration  
public class SecurityConfig extends WebSecurityConfiguration{

    /*@Bean
    @SuppressWarnings(value = {"unused"})
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())  // Deshabilita CSRF para APIs REST
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/**").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());
    
    return http.build();
    }*/
	
	protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .requestMatchers("/public/**").permitAll()  // Permite el acceso sin autenticación a rutas públicas
            .anyRequest().authenticated()  // Requiere autenticación para todas las demás rutas
            .and().csrf().disable();  // Deshabilita CSRF para facilitar la integración
    }

    @Bean
    public KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        return new KeycloakAuthenticationProvider();
    }
}
