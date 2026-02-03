package br.com.seplag.musicapi.api.v1.dto.regional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionalResponse {
    private Long id;
    private Long sourceId;
    private String nome;
    private String sigla;
    private Boolean ativo;
    private LocalDateTime syncedAt;
}
