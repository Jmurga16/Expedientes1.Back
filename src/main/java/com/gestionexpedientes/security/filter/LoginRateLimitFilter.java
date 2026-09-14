package com.gestionexpedientes.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionexpedientes.global.dto.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoginRateLimitFilter.class);
    private static final String LOGIN_PATH = "/auth/login";

    private final Map<String, Attempts> attemptsByIp = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final int maxAttempts;
    private final long windowSeconds;
    private final long blockSeconds;

    public LoginRateLimitFilter(@Value("${security.login.max-attempts:5}") int maxAttempts,
                                @Value("${security.login.window-seconds:300}") long windowSeconds,
                                @Value("${security.login.block-seconds:900}") long blockSeconds) {
        this.maxAttempts = maxAttempts;
        this.windowSeconds = windowSeconds;
        this.blockSeconds = blockSeconds;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        return !("POST".equalsIgnoreCase(req.getMethod()) && LOGIN_PATH.equals(req.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String ip = clientIp(req);
        Attempts attempts = attemptsByIp.get(ip);

        if (attempts != null && attempts.isBlocked(Instant.now())) {
            logger.warn("login bloqueado por rate limit: {}", ip);
            reject(res, attempts.secondsUntilUnblock(Instant.now()));
            return;
        }

        chain.doFilter(req, res);

        if (res.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            registerFailure(ip);
        } else if (res.getStatus() < HttpStatus.BAD_REQUEST.value()) {
            attemptsByIp.remove(ip);
        }
    }

    private void registerFailure(String ip) {
        Instant now = Instant.now();
        purgeExpired(now);
        attemptsByIp.compute(ip, (key, current) -> {
            Attempts attempts = (current == null || current.isWindowExpired(now, windowSeconds))
                    ? new Attempts(now)
                    : current;
            attempts.increment(now);
            if (attempts.getCount() >= maxAttempts) {
                attempts.blockUntil(now.plusSeconds(blockSeconds));
            }
            return attempts;
        });
    }

    private void purgeExpired(Instant now) {
        attemptsByIp.entrySet().removeIf(entry ->
                !entry.getValue().isBlocked(now) && entry.getValue().isWindowExpired(now, windowSeconds));
    }

    private void reject(HttpServletResponse res, long retryAfterSeconds) throws IOException {
        MessageDto dto = new MessageDto(HttpStatus.TOO_MANY_REQUESTS,
                "Demasiados intentos fallidos. Vuelva a intentarlo en " + Math.max(1, retryAfterSeconds / 60) + " minuto(s).");
        res.setStatus(dto.getStatus().value());
        res.setContentType("application/json");
        res.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        res.getWriter().write(objectMapper.writeValueAsString(dto));
        res.getWriter().flush();
    }

    private String clientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank())
            return forwarded.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    private static class Attempts {

        private Instant windowStart;
        private int count;
        private Instant blockedUntil;

        private Attempts(Instant windowStart) {
            this.windowStart = windowStart;
        }

        private void increment(Instant now) {
            if (count == 0)
                windowStart = now;
            count++;
        }

        private int getCount() {
            return count;
        }

        private void blockUntil(Instant until) {
            blockedUntil = until;
        }

        private boolean isBlocked(Instant now) {
            if (blockedUntil == null)
                return false;
            if (blockedUntil.isAfter(now))
                return true;
            blockedUntil = null;
            count = 0;
            return false;
        }

        private boolean isWindowExpired(Instant now, long windowSeconds) {
            return Duration.between(windowStart, now).getSeconds() >= windowSeconds;
        }

        private long secondsUntilUnblock(Instant now) {
            return blockedUntil == null ? 0 : Duration.between(now, blockedUntil).getSeconds();
        }
    }
}
