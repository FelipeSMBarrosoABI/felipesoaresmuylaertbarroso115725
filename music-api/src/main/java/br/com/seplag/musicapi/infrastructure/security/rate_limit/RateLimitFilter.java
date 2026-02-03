package br.com.seplag.musicapi.infrastructure.security.rate_limit;

import br.com.seplag.musicapi.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> rateLimitBuckets;
    private final RateLimitConfig rateLimitConfig;
    private final BucketKeyResolver bucketKeyResolver;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Skip rate limiting for public endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/") ||
            path.startsWith("/actuator/") ||
            path.startsWith("/swagger-ui/") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = bucketKeyResolver.resolve(request);
        Bucket bucket = rateLimitBuckets.computeIfAbsent(key, k -> rateLimitConfig.createNewBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests. Rate limit: 10 requests per minute.\"}");
        }
    }
}
