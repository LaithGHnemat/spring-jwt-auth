package com.laith.evolution.services;

import com.laith.evolution.annotations.BlacklistCheck;
import com.laith.evolution.annotations.RateLimit;
import org.springframework.stereotype.Service;

@Service
public class SensitiveInfoService {
    @RateLimit
    @BlacklistCheck
    public String getSensitiveInfo() {
        return "This is sensitive info. Access monitored by AOP.";
    }
}
