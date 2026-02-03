package br.com.seplag.musicapi.application.usecase;

import br.com.seplag.musicapi.api.v1.dto.regional.SyncResultResponse;
import br.com.seplag.musicapi.application.service.RegionalSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SyncRegionaisUseCase {

    private final RegionalSyncService regionalSyncService;

    public SyncResultResponse execute() {
        return regionalSyncService.sync();
    }
}
