package com.example.cloudintegrationapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @PostConstruct
    public void setAnonymousSecurityContext() {
        // Set anonymous SecurityContext for Docker environment
        System.out.println("SecurityConfig: @PostConstruct called");
        System.out.println("SecurityConfig: Active profiles: " + System.getenv("SPRING_PROFILES_ACTIVE"));
        System.out.println("SecurityConfig: isDockerEnvironment(): " + isDockerEnvironment());
        
        if (isDockerEnvironment()) {
            System.out.println("SecurityConfig: Setting anonymous security context");
            AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                "anonymous", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
            );
            SecurityContext anonymousContext = new SecurityContextImpl();
            anonymousContext.setAuthentication(anonymousToken);
            SecurityContextHolder.setContext(anonymousContext);
            System.out.println("SecurityConfig: Anonymous security context set successfully");
        }
    }

    private boolean isDockerEnvironment() {
        // Check if running in Docker container, development, or QA environment
        String activeProfiles = System.getenv("SPRING_PROFILES_ACTIVE");
        return activeProfiles != null && 
               (activeProfiles.contains("docker") || activeProfiles.contains("dev") || activeProfiles.contains("qa"));
    }


    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev", matchIfMissing = false)
    public SecurityFilterChain dockerFilterChain(HttpSecurity http) throws Exception {
        System.out.println("SecurityConfig: Creating dockerFilterChain for dev profile");
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // Allow all requests in Docker/Dev environment
            )
            .anonymous(anonymous -> anonymous
                .key("anonymous")
                .authorities(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
            );
        
        System.out.println("SecurityConfig: dockerFilterChain created successfully");
        return http.build();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "qa", matchIfMissing = false)
    public SecurityFilterChain qaFilterChain(HttpSecurity http) throws Exception {
        System.out.println("SecurityConfig: Creating qaFilterChain for qa profile");
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // Allow all requests in QA environment
            )
            .anonymous(anonymous -> anonymous
                .key("anonymous")
                .authorities(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
            );
        
        System.out.println("SecurityConfig: qaFilterChain created successfully");
        return http.build();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod", matchIfMissing = false)
    public SecurityFilterChain prodFilterChain(HttpSecurity http) throws Exception {
        System.out.println("SecurityConfig: Creating prodFilterChain for prod profile");
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/cloud/health", "/cloud/test", "/actuator/**", "/api/cloud/health", "/api/cloud/test").permitAll()  // Allow health checks and test endpoints
                .anyRequest().permitAll()  // Temporarily allow all requests for testing
            )
            .anonymous(anonymous -> anonymous
                .key("anonymous")
                .authorities(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
            );
        
        System.out.println("SecurityConfig: prodFilterChain created successfully");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
