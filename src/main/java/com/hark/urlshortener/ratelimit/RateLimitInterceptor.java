package com.hark.urlshortener.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor extends HandlerInterceptor {
    private final RateLimiter rateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String clientId = req.getRemoteAddr();
        if (!rateLimiter.isAllowed(clientId)) {
            res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
            res.getWriter().write("Rate limit exceeded. Try again later.");
            return false;
        }
        return true;
    }
}
