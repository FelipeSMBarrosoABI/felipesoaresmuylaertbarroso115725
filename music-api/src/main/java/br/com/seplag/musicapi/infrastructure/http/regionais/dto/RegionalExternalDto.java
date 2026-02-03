package br.com.seplag.musicapi.infrastructure.http.regionais.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionalExternalDto {
    private Long id;
    private String nome;
    private String sigla;
    private Boolean ativo;
}
