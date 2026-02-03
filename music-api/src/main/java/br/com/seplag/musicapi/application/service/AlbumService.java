package br.com.seplag.musicapi.application.service;

import br.com.seplag.musicapi.api.v1.dto.album.AlbumCreateRequest;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumPageResponse;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumResponse;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumUpdateRequest;
import br.com.seplag.musicapi.api.v1.mapper.AlbumMapper;
import br.com.seplag.musicapi.domain.model.Album;
import br.com.seplag.musicapi.domain.model.Artist;
import br.com.seplag.musicapi.infrastructure.persistence.repository.AlbumRepository;
import br.com.seplag.musicapi.infrastructure.persistence.repository.ArtistRepository;
import br.com.seplag.musicapi.infrastructure.persistence.specification.AlbumSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumMapper albumMapper;

    @Transactional(readOnly = true)
    public AlbumPageResponse findAll(String title, String genre, Integer year, Pageable pageable) {
        Specification<Album> spec = Specification
                .where(AlbumSpecifications.titleContains(title))
                .and(AlbumSpecifications.genreEquals(genre))
                .and(AlbumSpecifications.releaseYearEquals(year));

        Page<Album> page = albumRepository.findAll(spec, pageable);

        List<AlbumResponse> content = page.getContent().stream()
                .map(albumMapper::toResponse)
                .collect(Collectors.toList());

        return AlbumPageResponse.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public AlbumResponse findById(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id: " + id));
        return albumMapper.toResponse(album);
    }

    @Transactional
    public Album create(AlbumCreateRequest request) {
        Set<Artist> artists = new HashSet<>(artistRepository.findAllById(request.getArtistIds()));

        if (artists.size() != request.getArtistIds().size()) {
            throw new EntityNotFoundException("One or more artists not found");
        }

        Album album = Album.builder()
                .title(request.getTitle())
                .releaseYear(request.getReleaseYear())
                .genre(request.getGenre())
                .artists(artists)
                .build();

        return albumRepository.save(album);
    }

    @Transactional
    public AlbumResponse update(Long id, AlbumUpdateRequest request) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id: " + id));

        if (request.getTitle() != null) {
            album.setTitle(request.getTitle());
        }
        if (request.getReleaseYear() != null) {
            album.setReleaseYear(request.getReleaseYear());
        }
        if (request.getGenre() != null) {
            album.setGenre(request.getGenre());
        }
        if (request.getArtistIds() != null && !request.getArtistIds().isEmpty()) {
            Set<Artist> artists = new HashSet<>(artistRepository.findAllById(request.getArtistIds()));
            if (artists.size() != request.getArtistIds().size()) {
                throw new EntityNotFoundException("One or more artists not found");
            }
            album.setArtists(artists);
        }

        Album saved = albumRepository.save(album);
        return albumMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Album getEntityById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id: " + id));
    }
}
