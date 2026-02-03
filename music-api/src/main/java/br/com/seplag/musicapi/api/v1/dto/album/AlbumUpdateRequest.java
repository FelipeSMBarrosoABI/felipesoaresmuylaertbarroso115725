package br.com.seplag.musicapi.api.v1.dto.album;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumUpdateRequest {
    private String title;
    private Integer releaseYear;
    private String genre;
    private Set<Long> artistIds;
}
