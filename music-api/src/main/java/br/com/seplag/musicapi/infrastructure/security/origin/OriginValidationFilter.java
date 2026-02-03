package br.com.seplag.musicapi.infrastructure.security.origin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OriginValidationFilter extends OncePerRequestFilter {

    private final Set<String> allowedOrigins;

    public OriginValidationFilter(@Value("${app.cors.allowed-origins}") String allowedOriginsStr) {
        this.allowedOrigins = Arrays.stream(allowedOriginsStr.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        // Allow requests without Origin/Referer (same-origin, curl, etc.)
        if (origin == null && referer == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check Origin header
        if (origin != null && !isAllowedOrigin(origin)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Origin not allowed");
            return;
        }

        // Check Referer header if Origin is not present
        if (origin == null && !isAllowedReferer(referer)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Referer not allowed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowedOrigin(String origin) {
        return allowedOrigins.contains(origin);
    }

    private boolean isAllowedReferer(String referer) {
        return allowedOrigins.stream().anyMatch(referer::startsWith);
    }
}
