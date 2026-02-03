package br.com.seplag.musicapi.api.v1.dto.cover;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverResponse {
    private Long albumId;
    private String presignedUrl;
    private String contentType;
    private Long fileSize;
}
