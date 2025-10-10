package com.laith.evolution.security.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laith.evolution.dto.ErrorResponseDto;
import com.laith.evolution.security.filter.ClientJwtAuthenticationFilter;
/*
import com.laith.evolution.security.filter.UserJwtAuthenticationFilter;
*/
import com.laith.evolution.security.filter.GlobalJwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@RequiredArgsConstructor
@Order(2)
public class SecurityConfiguration {

    //Method Dependency Injection
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
    ClientJwtAuthenticationFilter clientFilter, GlobalJwtAuthenticationFilter globalJwtAuthenticationFilter)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/oauth/token").permitAll()
                        .requestMatchers("/api/data/client/**").hasRole("CLIENT")
                        .requestMatchers("/api/data/user/**").hasRole("USER")
                        .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(globalJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(clientFilter, GlobalJwtAuthenticationFilter.class)
              //  .addFilterBefore(userFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(getExceptionHandlingCustomizer());
        return http.build();
    }

    private Customizer<ExceptionHandlingConfigurer<HttpSecurity>> getExceptionHandlingCustomizer() {
        return ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    ObjectMapper mapper = new ObjectMapper();
                    response.getWriter().write(mapper.writeValueAsString(
                            ErrorResponseDto.builder()
                                    .error("unauthorized")
                                    .errorDescription("Token expired or invalid").build()
                    ));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    ObjectMapper mapper = new ObjectMapper();
                    response.getWriter().write(mapper.writeValueAsString(
                            ErrorResponseDto.builder()
                                    .error("forbidden")
                                    .errorDescription("Access denied: insufficient role").build()
                    ));
                });
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8005"));
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}