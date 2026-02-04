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
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumMapper albumMapper;

    @InjectMocks
    private AlbumService albumService;

    private Album testAlbum;
    private Artist testArtist;
    private AlbumResponse testAlbumResponse;

    @BeforeEach
    void setUp() {
        testArtist = Artist.builder()
                .id(1L)
                .name("The Beatles")
                .isSinger(false)
                .isBand(true)
                .build();

        testAlbum = Album.builder()
                .id(1L)
                .title("Abbey Road")
                .releaseYear(1969)
                .genre("Rock")
                .artists(new HashSet<>(Set.of(testArtist)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAlbumResponse = AlbumResponse.builder()
                .id(1L)
                .title("Abbey Road")
                .releaseYear(1969)
                .genre("Rock")
                .createdAt(testAlbum.getCreatedAt())
                .updatedAt(testAlbum.getUpdatedAt())
                .build();
    }

    @Test
    void findAll_ReturnsPageResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Album> albumPage = new PageImpl<>(List.of(testAlbum), pageable, 1);

        when(albumRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(albumPage);
        when(albumMapper.toResponse(testAlbum)).thenReturn(testAlbumResponse);

        AlbumPageResponse result = albumService.findAll(null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Abbey Road");
    }

    @Test
    void findAll_WithFilters_ReturnsFilteredResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Album> albumPage = new PageImpl<>(List.of(testAlbum), pageable, 1);

        when(albumRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(albumPage);
        when(albumMapper.toResponse(testAlbum)).thenReturn(testAlbumResponse);

        AlbumPageResponse result = albumService.findAll("Abbey", "Rock", 1969, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(albumRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findById_WithExistingId_ReturnsAlbum() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(albumMapper.toResponse(testAlbum)).thenReturn(testAlbumResponse);

        AlbumResponse result = albumService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Abbey Road");
    }

    @Test
    void findById_WithNonExistingId_ThrowsEntityNotFoundException() {
        when(albumRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Album not found");
    }

    @Test
    void create_WithValidRequest_ReturnsCreatedAlbum() {
        Set<Long> artistIds = Set.of(1L);
        AlbumCreateRequest request = new AlbumCreateRequest("Let It Be", 1970, "Rock", artistIds);

        Album newAlbum = Album.builder()
                .id(2L)
                .title("Let It Be")
                .releaseYear(1970)
                .genre("Rock")
                .artists(new HashSet<>(Set.of(testArtist)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(artistRepository.findAllById(artistIds)).thenReturn(List.of(testArtist));
        when(albumRepository.save(any(Album.class))).thenReturn(newAlbum);

        Album result = albumService.create(request);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("Let It Be");
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void create_WithNonExistingArtist_ThrowsEntityNotFoundException() {
        Set<Long> artistIds = Set.of(999L);
        AlbumCreateRequest request = new AlbumCreateRequest("Album", 2020, "Pop", artistIds);

        when(artistRepository.findAllById(artistIds)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> albumService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("artists not found");
    }

    @Test
    void update_WithExistingId_ReturnsUpdatedAlbum() {
        AlbumUpdateRequest request = new AlbumUpdateRequest();
        request.setTitle("Abbey Road (Remastered)");

        Album updatedAlbum = Album.builder()
                .id(1L)
                .title("Abbey Road (Remastered)")
                .releaseYear(1969)
                .genre("Rock")
                .artists(testAlbum.getArtists())
                .createdAt(testAlbum.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        AlbumResponse updatedResponse = AlbumResponse.builder()
                .id(1L)
                .title("Abbey Road (Remastered)")
                .releaseYear(1969)
                .genre("Rock")
                .build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(albumRepository.save(any(Album.class))).thenReturn(updatedAlbum);
        when(albumMapper.toResponse(updatedAlbum)).thenReturn(updatedResponse);

        AlbumResponse result = albumService.update(1L, request);

        assertThat(result.getTitle()).isEqualTo("Abbey Road (Remastered)");
    }

    @Test
    void update_WithNonExistingId_ThrowsEntityNotFoundException() {
        AlbumUpdateRequest request = new AlbumUpdateRequest();
        request.setTitle("Updated Title");

        when(albumRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.update(999L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Album not found");
    }

    @Test
    void update_WithNewArtists_UpdatesArtistRelationship() {
        Set<Long> newArtistIds = Set.of(2L);
        AlbumUpdateRequest request = new AlbumUpdateRequest();
        request.setArtistIds(newArtistIds);

        Artist newArtist = Artist.builder()
                .id(2L)
                .name("Queen")
                .isBand(true)
                .build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(artistRepository.findAllById(newArtistIds)).thenReturn(List.of(newArtist));
        when(albumRepository.save(any(Album.class))).thenReturn(testAlbum);
        when(albumMapper.toResponse(any(Album.class))).thenReturn(testAlbumResponse);

        albumService.update(1L, request);

        verify(artistRepository).findAllById(newArtistIds);
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void getEntityById_WithExistingId_ReturnsEntity() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));

        Album result = albumService.getEntityById(1L);

        assertThat(result).isEqualTo(testAlbum);
    }
}
