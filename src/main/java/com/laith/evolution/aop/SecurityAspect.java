package com.laith.evolution.aop;

import com.laith.evolution.annotations.BlacklistCheck;
import com.laith.evolution.annotations.RateLimit;
import com.laith.evolution.exceptions.InvalidTokenException;
import com.laith.evolution.exceptions.RateLimitExceededException;
import com.laith.evolution.exceptions.TokenBlacklistedException;
import com.laith.evolution.repositories.redis.BlacklistRepository;
import com.laith.evolution.repositories.jpa.ClientRepository;
import com.laith.evolution.repositories.redis.RateLimitRepository;
import com.laith.evolution.security.service.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Log
public class SecurityAspect {

    private final JwtUtility jwtUtility;
    private final RateLimitRepository rateLimitRepository;
    private final BlacklistRepository blacklistRepository;
    private final HttpServletRequest request;

    @Around("execution(* com.laith.evolution.services..*(..)) && " +
            "(@annotation(com.laith.evolution.annotations.RateLimit) || " +
            "@annotation(com.laith.evolution.annotations.BlacklistCheck))")
    public Object handleSecurity(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Aspect triggered for method: " + joinPoint.getSignature());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        BlacklistCheck blacklistCheck = method.getAnnotation(BlacklistCheck.class);

        String token = extractTokenFromContext();
        validateJwt(token);
        checkBlacklist(token, blacklistCheck);
        handleRateLimit(token, rateLimit);

        return joinPoint.proceed();
    }

    private String extractTokenFromContext() {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new SecurityException("JWT token not found in request header");
    }

    private void validateJwt(String token) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new InvalidTokenException("User not authenticated");
        }

        log.info("JWT token already validated by filter for user: " + auth.getName());
    }

    private void checkBlacklist(String token, BlacklistCheck blacklistCheck) {
        if (blacklistCheck != null && blacklistRepository.isBlacklisted(token)) {
            log.warning("Access denied for blacklisted token: " + token);
            throw new TokenBlacklistedException("Token is blacklisted");
        }
    }

    private void handleRateLimit(String token, RateLimit rateLimit) {
        if (rateLimit == null) return;
        String key = token + ":" + request.getRequestURI();
        int attempts = rateLimitRepository.increment(key, rateLimit.durationInSeconds());

        if (attempts > rateLimit.maxAttempts()) {
            long remainingSeconds = jwtUtility.getExpiresIn(token);
            long minutes = Math.max(1, remainingSeconds / 60);
            blacklistRepository.addToBlacklist(token, minutes);
           log.warning("Token " + token + " added to blacklist after exceeding rate limit");
            throw new RateLimitExceededException("Rate limit exceeded. Token blacklisted.");
        }
    }
}
