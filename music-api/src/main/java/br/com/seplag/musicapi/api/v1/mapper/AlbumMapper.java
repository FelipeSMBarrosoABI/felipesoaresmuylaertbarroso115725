package br.com.seplag.musicapi.api.v1.mapper;

import br.com.seplag.musicapi.api.v1.dto.album.AlbumResponse;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistResponse;
import br.com.seplag.musicapi.domain.model.Album;
import br.com.seplag.musicapi.infrastructure.storage.s3.PresignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlbumMapper {

    private final ArtistMapper artistMapper;
    private final PresignedUrlService presignedUrlService;

    public AlbumResponse toResponse(Album album) {
        String coverUrl = null;
        if (album.getCover() != null) {
            coverUrl = presignedUrlService.generatePresignedUrl(album.getCover().getObjectKey());
        }

        return AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .releaseYear(album.getReleaseYear())
                .genre(album.getGenre())
                .artists(album.getArtists().stream()
                        .map(artistMapper::toResponse)
                        .collect(Collectors.toSet()))
                .coverUrl(coverUrl)
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }

    public AlbumResponse toResponseWithoutCover(Album album) {
        return AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .releaseYear(album.getReleaseYear())
                .genre(album.getGenre())
                .artists(album.getArtists().stream()
                        .map(artistMapper::toResponse)
                        .collect(Collectors.toSet()))
                .coverUrl(null)
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }
}
