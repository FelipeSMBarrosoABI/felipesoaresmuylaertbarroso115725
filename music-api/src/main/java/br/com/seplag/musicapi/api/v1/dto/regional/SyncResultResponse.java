package br.com.seplag.musicapi.api.v1.dto.regional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResultResponse {
    private int created;
    private int updated;
    private int total;
    private String message;
}
