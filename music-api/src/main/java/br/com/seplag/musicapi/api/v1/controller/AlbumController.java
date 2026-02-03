package br.com.seplag.musicapi.api.v1.controller;

import br.com.seplag.musicapi.api.v1.dto.album.AlbumCreateRequest;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumPageResponse;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumResponse;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumUpdateRequest;
import br.com.seplag.musicapi.application.service.AlbumService;
import br.com.seplag.musicapi.application.usecase.CreateAlbumUseCase;
import br.com.seplag.musicapi.common.util.PageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final CreateAlbumUseCase createAlbumUseCase;

    @GetMapping
    public ResponseEntity<AlbumPageResponse> findAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        Pageable pageable = PageUtils.createPageable(page, size, sortBy, sortDir);
        AlbumPageResponse albums = albumService.findAll(title, genre, year, pageable);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> findById(@PathVariable Long id) {
        AlbumResponse album = albumService.findById(id);
        return ResponseEntity.ok(album);
    }

    @PostMapping
    public ResponseEntity<AlbumResponse> create(@Valid @RequestBody AlbumCreateRequest request) {
        AlbumResponse album = createAlbumUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(album);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponse> update(
            @PathVariable Long id,
            @RequestBody AlbumUpdateRequest request
    ) {
        AlbumResponse album = albumService.update(id, request);
        return ResponseEntity.ok(album);
    }
}
