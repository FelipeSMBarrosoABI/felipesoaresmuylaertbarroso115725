package br.com.seplag.musicapi.api.v1.dto.cover;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadCoverResponse {
    private Long albumId;
    private String objectKey;
    private String presignedUrl;
}
