package br.com.seplag.musicapi.api.v1.dto.artist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "isSinger is required")
    private Boolean isSinger;

    @NotNull(message = "isBand is required")
    private Boolean isBand;
}
