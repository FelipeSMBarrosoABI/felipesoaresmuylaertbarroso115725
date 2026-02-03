package br.com.seplag.musicapi.api.v1.controller;

import br.com.seplag.musicapi.api.v1.dto.artist.ArtistCreateRequest;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistResponse;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistUpdateRequest;
import br.com.seplag.musicapi.application.service.ArtistService;
import br.com.seplag.musicapi.common.util.PageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<Page<ArtistResponse>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isSinger,
            @RequestParam(required = false) Boolean isBand,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        Pageable pageable = PageUtils.createPageable(page, size, sortBy, sortDir);
        Page<ArtistResponse> artists = artistService.findAll(name, isSinger, isBand, pageable);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> findById(@PathVariable Long id) {
        ArtistResponse artist = artistService.findById(id);
        return ResponseEntity.ok(artist);
    }

    @PostMapping
    public ResponseEntity<ArtistResponse> create(@Valid @RequestBody ArtistCreateRequest request) {
        ArtistResponse artist = artistService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(artist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistResponse> update(
            @PathVariable Long id,
            @RequestBody ArtistUpdateRequest request
    ) {
        ArtistResponse artist = artistService.update(id, request);
        return ResponseEntity.ok(artist);
    }
}
