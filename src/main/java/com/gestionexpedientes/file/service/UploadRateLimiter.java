package com.gestionexpedientes.file.service;

import com.gestionexpedientes.global.exceptions.TooManyRequestsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UploadRateLimiter {

    private final Map<Integer, Deque<Instant>> uploadsByUser = new ConcurrentHashMap<>();

    private final int maxUploads;
    private final long windowSeconds;

    public UploadRateLimiter(@Value("${security.upload.max-per-window:10}") int maxUploads,
                             @Value("${security.upload.window-seconds:600}") long windowSeconds) {
        this.maxUploads = maxUploads;
        this.windowSeconds = windowSeconds;
    }

    public void register(int idUsuario) {
        Instant now = Instant.now();
        Deque<Instant> uploads = uploadsByUser.computeIfAbsent(idUsuario, id -> new ArrayDeque<>());

        synchronized (uploads) {
            Instant windowStart = now.minusSeconds(windowSeconds);
            while (!uploads.isEmpty() && uploads.peekFirst().isBefore(windowStart))
                uploads.pollFirst();

            if (uploads.size() >= maxUploads) {
                long retryAfter = Math.max(1, Duration.between(windowStart, uploads.peekFirst()).getSeconds());
                throw new TooManyRequestsException(
                        "Alcanzó el límite de " + maxUploads + " archivos subidos. Vuelva a intentarlo en "
                                + Math.max(1, retryAfter / 60) + " minuto(s).", retryAfter);
            }
            uploads.addLast(now);
        }
    }
}
