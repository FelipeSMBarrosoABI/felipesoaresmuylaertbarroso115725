package br.com.seplag.musicapi.infrastructure.http.regionais;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.regionais")
public class RegionaisClientConfig {
    private String url;
}
