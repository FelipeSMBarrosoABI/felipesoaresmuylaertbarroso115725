package br.com.seplag.musicapi.api.v1.dto.album;

import br.com.seplag.musicapi.api.v1.dto.artist.ArtistResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponse {
    private Long id;
    private String title;
    private Integer releaseYear;
    private String genre;
    private Set<ArtistResponse> artists;
    private String coverUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
