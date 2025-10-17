package com.laith.evolution.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    int maxAttempts() default 5;
    long durationInSeconds() default 60;
}