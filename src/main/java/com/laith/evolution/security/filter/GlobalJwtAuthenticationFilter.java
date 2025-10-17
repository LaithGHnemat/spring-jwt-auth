package com.laith.evolution.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laith.evolution.dto.ErrorResponseDto;
import com.laith.evolution.model.Client;
import com.laith.evolution.repositories.jpa.ClientRepository;
import com.laith.evolution.security.model.ClientDetailsImpl;
import com.laith.evolution.security.service.JwtUtility;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Log
@Component
@RequiredArgsConstructor
public class GlobalJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtService;
    private final ObjectMapper objectMapper;
    private final ClientRepository clientRepository;
    private static final List<String> EXCLUDED_PATHS = List.of("/api/oauth/token",
            "/api/oauth/refresh");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDED_PATHS.stream().anyMatch(request.getServletPath()::startsWith);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7).trim();

        try {
            String clientId = jwtService.extractUsername(jwt);

            if (Objects.nonNull(clientId) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                Client client = clientRepository.findByClientId(clientId)
                        .orElseThrow(() -> new BadCredentialsException("Client not found"));

                if (!jwtService.isTokenValid(jwt, new ClientDetailsImpl(client))) {
                    sendErrorResponse(response, "Invalid token",
                            HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                List<SimpleGrantedAuthority> authorities = mapRolesAndPermissions(client);

                SecurityContextHolder.getContext().setAuthentication(
               new UsernamePasswordAuthenticationToken(client, null, authorities));

                log.info(() -> "JWT token is valid for client: " + clientId);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warning("JWT expired: " + e.getMessage());
            sendErrorResponse(response, "Token expired",
                    HttpServletResponse.SC_UNAUTHORIZED);

        } catch (BadCredentialsException e) {
            sendErrorResponse(response, "Invalid token",
                    HttpServletResponse.SC_UNAUTHORIZED);

        }
        catch (JwtException e) {
            log.warning("Invalid JWT: " + e.getMessage());
            sendErrorResponse(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED);
        }

        catch (Exception e) {
            log.severe("Server error: " + e.getMessage());
            sendErrorResponse(response, "Server error",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private List<SimpleGrantedAuthority> mapRolesAndPermissions(Client client) {
        return Stream.concat(
                client.getRoles().stream()
                        .map(role ->
                       new SimpleGrantedAuthority("ROLE_" + role.getName())),
                client.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(p ->
                       new SimpleGrantedAuthority(p.getName().name()))).toList();
    }
    private void sendErrorResponse(HttpServletResponse response,
        String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                ErrorResponseDto.builder()
                  .error(status == HttpServletResponse.SC_UNAUTHORIZED ?
                   "unauthorized" : "server_error")
                   .errorDescription(message)
                   .build()));
    }
}