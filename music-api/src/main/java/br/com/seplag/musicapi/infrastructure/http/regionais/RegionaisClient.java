package br.com.seplag.musicapi.infrastructure.http.regionais;

import br.com.seplag.musicapi.infrastructure.http.regionais.dto.RegionalExternalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegionaisClient {

    private final RestClient restClient;
    private final RegionaisClientConfig config;

    public List<RegionalExternalDto> fetchRegionais() {
        try {
            List<RegionalExternalDto> result = restClient.get()
                    .uri(config.getUrl())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            return result != null ? result : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to fetch regionais from external API: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
