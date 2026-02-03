package br.com.seplag.musicapi.application.service;

import br.com.seplag.musicapi.api.v1.dto.regional.SyncResultResponse;
import br.com.seplag.musicapi.domain.model.Regional;
import br.com.seplag.musicapi.infrastructure.http.regionais.RegionaisClient;
import br.com.seplag.musicapi.infrastructure.http.regionais.dto.RegionalExternalDto;
import br.com.seplag.musicapi.infrastructure.persistence.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionalSyncService {

    private final RegionaisClient regionaisClient;
    private final RegionalRepository regionalRepository;

    @Transactional
    public SyncResultResponse sync() {
        List<RegionalExternalDto> externalRegionais = regionaisClient.fetchRegionais();

        if (externalRegionais.isEmpty()) {
            return SyncResultResponse.builder()
                    .created(0)
                    .updated(0)
                    .total(0)
                    .message("No regionais found in external API")
                    .build();
        }

        int created = 0;
        int updated = 0;

        for (RegionalExternalDto dto : externalRegionais) {
            Optional<Regional> existing = regionalRepository.findBySourceId(dto.getId());

            if (existing.isPresent()) {
                Regional regional = existing.get();
                regional.setNome(dto.getNome());
                regional.setSigla(dto.getSigla());
                regional.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
                regional.setSyncedAt(LocalDateTime.now());
                regionalRepository.save(regional);
                updated++;
            } else {
                Regional regional = Regional.builder()
                        .sourceId(dto.getId())
                        .nome(dto.getNome())
                        .sigla(dto.getSigla())
                        .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                        .build();
                regionalRepository.save(regional);
                created++;
            }
        }

        log.info("Regional sync completed: created={}, updated={}, total={}", created, updated, externalRegionais.size());

        return SyncResultResponse.builder()
                .created(created)
                .updated(updated)
                .total(externalRegionais.size())
                .message("Sync completed successfully")
                .build();
    }
}
