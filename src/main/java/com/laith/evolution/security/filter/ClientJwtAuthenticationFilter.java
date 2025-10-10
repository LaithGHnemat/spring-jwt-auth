package com.laith.evolution.security.filter;
import com.laith.evolution.exceptions.InvalidTokenException;
import com.laith.evolution.exceptions.NotFoundClientException;
import com.laith.evolution.model.Client;
import com.laith.evolution.repositories.ClientRepository;
import com.laith.evolution.security.service.JwtUtility;
import com.laith.evolution.security.model.ClientDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Log
@Component
@RequiredArgsConstructor
public class ClientJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtService;
    private final ClientRepository clientRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/api/data/client/");
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

        try {
            final String jwt = authHeader.substring(7).trim();
            String clientId = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (Objects.nonNull(clientId) && Objects.isNull(authentication)) {
                Client client = clientRepository.findByClientId(clientId)
                        .orElseThrow(() -> new InvalidTokenException("Client not found for token"));

                if (jwtService.isTokenValid(jwt, new ClientDetailsImpl(client))) {
                    log.info(() -> "JWT token is valid for client: " + clientId);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    client,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_CLIENT")));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.fine(() -> "Authentication set in SecurityContext for client: " + clientId);
                } else {
                    throw new InvalidTokenException("Token is expired or invalid");
                }
            }

            filterChain.doFilter(request, response);

        } catch (InvalidTokenException e) {
            log.warning("Invalid token: " + e.getMessage());
            throw e; // GlobalExceptionHandler يمسكه
        } catch (Exception exception) {
            log.severe("Exception while processing client JWT: " + exception.getMessage());
            throw new InvalidTokenException("Token processing failed");
            // handlerExceptionResolver.
            // resolveException(request, response, null, exception); todo this convert to global exception
            // cuze it start from the controller so cant handel the exception on filter
        }
    }

}
