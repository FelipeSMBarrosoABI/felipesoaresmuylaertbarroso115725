package br.com.seplag.musicapi.api.v1.dto.artist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistUpdateRequest {
    private String name;
    private Boolean isSinger;
    private Boolean isBand;
}
