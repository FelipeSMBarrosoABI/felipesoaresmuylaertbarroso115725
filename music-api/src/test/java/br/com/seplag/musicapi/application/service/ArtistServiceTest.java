package br.com.seplag.musicapi.application.service;

import br.com.seplag.musicapi.api.v1.dto.artist.ArtistCreateRequest;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistResponse;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistUpdateRequest;
import br.com.seplag.musicapi.api.v1.mapper.ArtistMapper;
import br.com.seplag.musicapi.domain.model.Artist;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistMapper artistMapper;

    @InjectMocks
    private ArtistService artistService;

    private Artist testArtist;
    private ArtistResponse testArtistResponse;

    @BeforeEach
    void setUp() {
        testArtist = Artist.builder()
                .id(1L)
                .name("The Beatles")
                .isSinger(false)
                .isBand(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testArtistResponse = ArtistResponse.builder()
                .id(1L)
                .name("The Beatles")
                .isSinger(false)
                .isBand(true)
                .createdAt(testArtist.getCreatedAt())
                .updatedAt(testArtist.getUpdatedAt())
                .build();
    }

    @Test
    void findAll_ReturnsPageOfArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> artistPage = new PageImpl<>(List.of(testArtist), pageable, 1);

        when(artistRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(artistPage);
        when(artistMapper.toResponse(testArtist)).thenReturn(testArtistResponse);

        Page<ArtistResponse> result = artistService.findAll(null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("The Beatles");
    }

    @Test
    void findAll_WithNameFilter_ReturnsFilteredResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> artistPage = new PageImpl<>(List.of(testArtist), pageable, 1);

        when(artistRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(artistPage);
        when(artistMapper.toResponse(testArtist)).thenReturn(testArtistResponse);

        Page<ArtistResponse> result = artistService.findAll("Beatles", null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(artistRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findById_WithExistingId_ReturnsArtist() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(artistMapper.toResponse(testArtist)).thenReturn(testArtistResponse);

        ArtistResponse result = artistService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("The Beatles");
    }

    @Test
    void findById_WithNonExistingId_ThrowsEntityNotFoundException() {
        when(artistRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Artist not found");
    }

    @Test
    void create_WithValidRequest_ReturnsCreatedArtist() {
        ArtistCreateRequest request = new ArtistCreateRequest("Queen", false, true);
        Artist newArtist = Artist.builder()
                .name("Queen")
                .isSinger(false)
                .isBand(true)
                .build();
        Artist savedArtist = Artist.builder()
                .id(2L)
                .name("Queen")
                .isSinger(false)
                .isBand(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArtistResponse savedResponse = ArtistResponse.builder()
                .id(2L)
                .name("Queen")
                .isSinger(false)
                .isBand(true)
                .build();

        when(artistMapper.toEntity(request)).thenReturn(newArtist);
        when(artistRepository.save(newArtist)).thenReturn(savedArtist);
        when(artistMapper.toResponse(savedArtist)).thenReturn(savedResponse);

        ArtistResponse result = artistService.create(request);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Queen");
        verify(artistRepository).save(newArtist);
    }

    @Test
    void update_WithExistingId_ReturnsUpdatedArtist() {
        ArtistUpdateRequest request = new ArtistUpdateRequest();
        request.setName("The Beatles (Updated)");

        Artist updatedArtist = Artist.builder()
                .id(1L)
                .name("The Beatles (Updated)")
                .isSinger(false)
                .isBand(true)
                .createdAt(testArtist.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        ArtistResponse updatedResponse = ArtistResponse.builder()
                .id(1L)
                .name("The Beatles (Updated)")
                .isSinger(false)
                .isBand(true)
                .build();

        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(updatedArtist);
        when(artistMapper.toResponse(updatedArtist)).thenReturn(updatedResponse);

        ArtistResponse result = artistService.update(1L, request);

        assertThat(result.getName()).isEqualTo("The Beatles (Updated)");
    }

    @Test
    void update_WithNonExistingId_ThrowsEntityNotFoundException() {
        ArtistUpdateRequest request = new ArtistUpdateRequest();
        request.setName("Updated Name");

        when(artistRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.update(999L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Artist not found");
    }

    @Test
    void getEntityById_WithExistingId_ReturnsEntity() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));

        Artist result = artistService.getEntityById(1L);

        assertThat(result).isEqualTo(testArtist);
    }
}
