package br.com.seplag.musicapi.api.v1.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private Integer releaseYear;

    private String genre;

    @NotEmpty(message = "At least one artist is required")
    private Set<Long> artistIds;
}
