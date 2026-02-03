package br.com.seplag.musicapi.api.v1.controller;

import br.com.seplag.musicapi.api.v1.dto.regional.SyncResultResponse;
import br.com.seplag.musicapi.application.usecase.SyncRegionaisUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/regionais")
@RequiredArgsConstructor
public class RegionalController {

    private final SyncRegionaisUseCase syncRegionaisUseCase;

    @PostMapping("/sync")
    public ResponseEntity<SyncResultResponse> sync() {
        SyncResultResponse result = syncRegionaisUseCase.execute();
        return ResponseEntity.ok(result);
    }
}
