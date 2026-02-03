package br.com.seplag.musicapi.api.v1.mapper;

import br.com.seplag.musicapi.api.v1.dto.artist.ArtistCreateRequest;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistResponse;
import br.com.seplag.musicapi.domain.model.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {

    public ArtistResponse toResponse(Artist artist) {
        return ArtistResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .isSinger(artist.getIsSinger())
                .isBand(artist.getIsBand())
                .createdAt(artist.getCreatedAt())
                .updatedAt(artist.getUpdatedAt())
                .build();
    }

    public Artist toEntity(ArtistCreateRequest request) {
        return Artist.builder()
                .name(request.getName())
                .isSinger(request.getIsSinger())
                .isBand(request.getIsBand())
                .build();
    }
}
