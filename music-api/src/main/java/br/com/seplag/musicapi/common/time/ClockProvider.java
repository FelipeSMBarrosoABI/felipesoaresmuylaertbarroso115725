package br.com.seplag.musicapi.common.time;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class ClockProvider {

    private final Clock clock;

    public ClockProvider() {
        this.clock = Clock.systemDefaultZone();
    }

    public ClockProvider(Clock clock) {
        this.clock = clock;
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public Clock getClock() {
        return clock;
    }
}
