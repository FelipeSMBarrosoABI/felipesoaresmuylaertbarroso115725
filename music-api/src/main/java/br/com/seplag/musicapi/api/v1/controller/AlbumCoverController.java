package br.com.seplag.musicapi.api.v1.controller;

import br.com.seplag.musicapi.api.v1.dto.cover.CoverResponse;
import br.com.seplag.musicapi.api.v1.dto.cover.UploadCoverResponse;
import br.com.seplag.musicapi.application.service.AlbumCoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/albums/{albumId}/cover")
@RequiredArgsConstructor
public class AlbumCoverController {

    private final AlbumCoverService albumCoverService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadCoverResponse> uploadCover(
            @PathVariable Long albumId,
            @RequestParam("file") MultipartFile file
    ) {
        UploadCoverResponse response = albumCoverService.uploadCover(albumId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<CoverResponse> getCover(@PathVariable Long albumId) {
        CoverResponse response = albumCoverService.getCover(albumId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCover(@PathVariable Long albumId) {
        albumCoverService.deleteCover(albumId);
        return ResponseEntity.noContent().build();
    }
}
