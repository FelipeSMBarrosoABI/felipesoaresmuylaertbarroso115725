package br.com.seplag.musicapi.application.service;

import br.com.seplag.musicapi.api.v1.dto.artist.ArtistCreateRequest;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistResponse;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistUpdateRequest;
import br.com.seplag.musicapi.api.v1.mapper.ArtistMapper;
import br.com.seplag.musicapi.domain.model.Artist;
import br.com.seplag.musicapi.infrastructure.persistence.repository.ArtistRepository;
import br.com.seplag.musicapi.infrastructure.persistence.specification.ArtistSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Transactional(readOnly = true)
    public Page<ArtistResponse> findAll(String name, Boolean isSinger, Boolean isBand, Pageable pageable) {
        Specification<Artist> spec = Specification
                .where(ArtistSpecifications.nameContains(name))
                .and(ArtistSpecifications.isSinger(isSinger))
                .and(ArtistSpecifications.isBand(isBand));

        return artistRepository.findAll(spec, pageable)
                .map(artistMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ArtistResponse findById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));
        return artistMapper.toResponse(artist);
    }

    @Transactional
    public ArtistResponse create(ArtistCreateRequest request) {
        Artist artist = artistMapper.toEntity(request);
        Artist saved = artistRepository.save(artist);
        return artistMapper.toResponse(saved);
    }

    @Transactional
    public ArtistResponse update(Long id, ArtistUpdateRequest request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));

        if (request.getName() != null) {
            artist.setName(request.getName());
        }
        if (request.getIsSinger() != null) {
            artist.setIsSinger(request.getIsSinger());
        }
        if (request.getIsBand() != null) {
            artist.setIsBand(request.getIsBand());
        }

        Artist saved = artistRepository.save(artist);
        return artistMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Artist getEntityById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));
    }
}
