package br.com.seplag.musicapi.config;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {
    // Health probes are configured via application.yml
    // management.endpoint.health.probes.enabled=true enables /actuator/health/liveness and /actuator/health/readiness
}
