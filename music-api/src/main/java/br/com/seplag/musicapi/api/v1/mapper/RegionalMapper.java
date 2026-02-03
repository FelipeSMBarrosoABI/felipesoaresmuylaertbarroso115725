package br.com.seplag.musicapi.api.v1.mapper;

import br.com.seplag.musicapi.api.v1.dto.regional.RegionalResponse;
import br.com.seplag.musicapi.domain.model.Regional;
import br.com.seplag.musicapi.infrastructure.http.regionais.dto.RegionalExternalDto;
import org.springframework.stereotype.Component;

@Component
public class RegionalMapper {

    public RegionalResponse toResponse(Regional regional) {
        return RegionalResponse.builder()
                .id(regional.getId())
                .sourceId(regional.getSourceId())
                .nome(regional.getNome())
                .sigla(regional.getSigla())
                .ativo(regional.getAtivo())
                .syncedAt(regional.getSyncedAt())
                .build();
    }

    public Regional toEntity(RegionalExternalDto dto) {
        return Regional.builder()
                .sourceId(dto.getId())
                .nome(dto.getNome())
                .sigla(dto.getSigla())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .build();
    }
}
