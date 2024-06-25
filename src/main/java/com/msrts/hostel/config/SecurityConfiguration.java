package com.msrts.hostel.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.msrts.hostel.constant.Permission.*;
import static com.msrts.hostel.constant.Role.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**",
            "/api/v1/hostel/**",
            "/api/v1/room/**",
            "/api/v1/tenant/**",
            "/api/v1/expense/**",
            "/api/v1/payment/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"};

    @Autowired
    private final JwtAuthenticationFilter jwtAuthFilter;
    @Autowired
    private final AuthenticationProvider authenticationProvider;
    @Autowired
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/api/v1/auth/**").hasAnyRole(ADMIN.name(), MANAGER.name(),USER.name())
                                .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers("/api/v1/hostel/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(GET, "/api/v1/hostel/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/hostel/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER.name())
                                .requestMatchers(PUT, "/api/v1/hostel/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER.name())
                                .requestMatchers(DELETE, "/api/v1/hostel/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(), USER.name())
                                .requestMatchers("/api/v1/room/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(GET, "/api/v1/room/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/room/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER.name())
                                .requestMatchers(PUT, "/api/v1/room/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER.name())
                                .requestMatchers(DELETE, "/api/v1/room/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(), USER.name())
                                .requestMatchers("/api/v1/tenant/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(GET, "/api/v1/tenant/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/tenant/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER.name())
                                .requestMatchers(PUT, "/api/v1/tenant/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER.name())
                                .requestMatchers(DELETE, "/api/v1/tenant/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(), USER.name())
                                .requestMatchers("/api/v1/expense/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(GET, "/api/v1/expense/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/expense/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER.name())
                                .requestMatchers(PUT, "/api/v1/expense/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER.name())
                                .requestMatchers(DELETE, "/api/v1/expense/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(), USER.name())
                                .requestMatchers("/api/v1/payment/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(GET, "/api/v1/payment/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/payment/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER.name())
                                .requestMatchers(PUT, "/api/v1/payment/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER.name())
                                .requestMatchers(DELETE, "/api/v1/payment/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(), USER.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
        ;

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders(HttpHeaders.CONTENT_TYPE,
                                HttpHeaders.AUTHORIZATION);
            }
        };
    }
}
