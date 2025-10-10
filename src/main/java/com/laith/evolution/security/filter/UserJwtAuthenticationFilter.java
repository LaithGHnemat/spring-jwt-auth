/*
package com.laith.evolution.security.filter;

import com.laith.evolution.security.service.JwtUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Log
@Component
@RequiredArgsConstructor
@Order(2)
public class UserJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtService;

    private final UserDetailsService userDetailsService;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.fine("No Authorization header or does not start with Bearer, " +
                    "skipping authentication.");
            filterChain.doFilter(request, response);
            return;
        }

        try {

            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            log.fine(() -> "Extracted username from JWT: " + userEmail);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if ( Objects.nonNull(userEmail) && Objects.isNull(authentication)) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.info(() -> "JWT token is valid for user: " + userEmail);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.fine(() -> "Authentication set in SecurityContext for user: " + userEmail);
                } else {
                    log.warning(() -> "Invalid JWT token for user: " + userEmail);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.severe("Exception while processing JWT: " + exception.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
*/
