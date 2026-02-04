package br.com.seplag.musicapi.application.service;

import br.com.seplag.musicapi.api.v1.dto.regional.SyncResultResponse;
import br.com.seplag.musicapi.domain.model.Regional;
import br.com.seplag.musicapi.infrastructure.http.regionais.RegionaisClient;
import br.com.seplag.musicapi.infrastructure.http.regionais.dto.RegionalExternalDto;
import br.com.seplag.musicapi.infrastructure.persistence.repository.RegionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalSyncServiceTest {

    @Mock
    private RegionaisClient regionaisClient;

    @Mock
    private RegionalRepository regionalRepository;

    @InjectMocks
    private RegionalSyncService regionalSyncService;

    private RegionalExternalDto externalDto1;
    private RegionalExternalDto externalDto2;
    private Regional existingRegional;

    @BeforeEach
    void setUp() {
        externalDto1 = new RegionalExternalDto(1L, "Regional Norte", "RN", true);
        externalDto2 = new RegionalExternalDto(2L, "Regional Sul", "RS", true);

        existingRegional = Regional.builder()
                .id(100L)
                .sourceId(1L)
                .nome("Regional Norte Old")
                .sigla("RNO")
                .ativo(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .syncedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void sync_WithEmptyExternalApi_ReturnsZeroResults() {
        when(regionaisClient.fetchRegionais()).thenReturn(Collections.emptyList());

        SyncResultResponse result = regionalSyncService.sync();

        assertThat(result.getCreated()).isZero();
        assertThat(result.getUpdated()).isZero();
        assertThat(result.getTotal()).isZero();
        assertThat(result.getMessage()).contains("No regionais found");
        verify(regionalRepository, never()).save(any());
    }

    @Test
    void sync_WithNewRegionais_CreatesAll() {
        when(regionaisClient.fetchRegionais()).thenReturn(List.of(externalDto1, externalDto2));
        when(regionalRepository.findBySourceId(1L)).thenReturn(Optional.empty());
        when(regionalRepository.findBySourceId(2L)).thenReturn(Optional.empty());
        when(regionalRepository.save(any(Regional.class))).thenAnswer(i -> i.getArgument(0));

        SyncResultResponse result = regionalSyncService.sync();

        assertThat(result.getCreated()).isEqualTo(2);
        assertThat(result.getUpdated()).isZero();
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getMessage()).contains("successfully");
        verify(regionalRepository, times(2)).save(any(Regional.class));
    }

    @Test
    void sync_WithExistingRegional_UpdatesIt() {
        when(regionaisClient.fetchRegionais()).thenReturn(List.of(externalDto1));
        when(regionalRepository.findBySourceId(1L)).thenReturn(Optional.of(existingRegional));
        when(regionalRepository.save(any(Regional.class))).thenAnswer(i -> i.getArgument(0));

        SyncResultResponse result = regionalSyncService.sync();

        assertThat(result.getCreated()).isZero();
        assertThat(result.getUpdated()).isEqualTo(1);
        assertThat(result.getTotal()).isEqualTo(1);
        verify(regionalRepository).save(existingRegional);
        assertThat(existingRegional.getNome()).isEqualTo("Regional Norte");
        assertThat(existingRegional.getSigla()).isEqualTo("RN");
    }

    @Test
    void sync_WithMixedRegionais_CreatesAndUpdates() {
        when(regionaisClient.fetchRegionais()).thenReturn(List.of(externalDto1, externalDto2));
        when(regionalRepository.findBySourceId(1L)).thenReturn(Optional.of(existingRegional));
        when(regionalRepository.findBySourceId(2L)).thenReturn(Optional.empty());
        when(regionalRepository.save(any(Regional.class))).thenAnswer(i -> i.getArgument(0));

        SyncResultResponse result = regionalSyncService.sync();

        assertThat(result.getCreated()).isEqualTo(1);
        assertThat(result.getUpdated()).isEqualTo(1);
        assertThat(result.getTotal()).isEqualTo(2);
    }

    @Test
    void sync_WithNullAtivo_DefaultsToTrue() {
        RegionalExternalDto dtoWithNullAtivo = new RegionalExternalDto(3L, "Regional Centro", "RC", null);
        when(regionaisClient.fetchRegionais()).thenReturn(List.of(dtoWithNullAtivo));
        when(regionalRepository.findBySourceId(3L)).thenReturn(Optional.empty());
        when(regionalRepository.save(any(Regional.class))).thenAnswer(i -> {
            Regional saved = i.getArgument(0);
            assertThat(saved.getAtivo()).isTrue();
            return saved;
        });

        SyncResultResponse result = regionalSyncService.sync();

        assertThat(result.getCreated()).isEqualTo(1);
        verify(regionalRepository).save(any(Regional.class));
    }

    @Test
    void sync_WithExistingRegionalAndNullAtivo_DefaultsToTrue() {
        RegionalExternalDto dtoWithNullAtivo = new RegionalExternalDto(1L, "Regional Norte Updated", "RNU", null);
        when(regionaisClient.fetchRegionais()).thenReturn(List.of(dtoWithNullAtivo));
        when(regionalRepository.findBySourceId(1L)).thenReturn(Optional.of(existingRegional));
        when(regionalRepository.save(any(Regional.class))).thenAnswer(i -> i.getArgument(0));

        regionalSyncService.sync();

        assertThat(existingRegional.getAtivo()).isTrue();
        assertThat(existingRegional.getNome()).isEqualTo("Regional Norte Updated");
    }
}
