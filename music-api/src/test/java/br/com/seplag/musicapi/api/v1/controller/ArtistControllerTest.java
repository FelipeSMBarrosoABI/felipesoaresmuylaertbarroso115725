package br.com.seplag.musicapi.api.v1.controller;

import br.com.seplag.musicapi.api.v1.dto.artist.ArtistCreateRequest;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistResponse;
import br.com.seplag.musicapi.api.v1.dto.artist.ArtistUpdateRequest;
import br.com.seplag.musicapi.application.service.ArtistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ArtistControllerTest {

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private ArtistController artistController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ArtistResponse testArtistResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(artistController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testArtistResponse = ArtistResponse.builder()
                .id(1L)
                .name("The Beatles")
                .isSinger(false)
                .isBand(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_ReturnsPageOfArtists() throws Exception {
        Page<ArtistResponse> page = new PageImpl<>(List.of(testArtistResponse));
        when(artistService.findAll(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("The Beatles"));
    }

    @Test
    void findAll_WithFilters_ReturnsFilteredResults() throws Exception {
        Page<ArtistResponse> page = new PageImpl<>(List.of(testArtistResponse));
        when(artistService.findAll(eq("Beatles"), eq(false), eq(true), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/artists")
                        .param("name", "Beatles")
                        .param("isSinger", "false")
                        .param("isBand", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("The Beatles"));
    }

    @Test
    void findAll_WithPagination_ReturnsPagedResults() throws Exception {
        Page<ArtistResponse> page = new PageImpl<>(List.of(testArtistResponse));
        when(artistService.findAll(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/artists")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void findById_WithExistingId_ReturnsArtist() throws Exception {
        when(artistService.findById(1L)).thenReturn(testArtistResponse);

        mockMvc.perform(get("/api/v1/artists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("The Beatles"));
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        when(artistService.findById(999L)).thenThrow(new EntityNotFoundException("Artist not found"));

        mockMvc.perform(get("/api/v1/artists/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidRequest_ReturnsCreatedArtist() throws Exception {
        ArtistCreateRequest request = new ArtistCreateRequest("Queen", false, true);
        ArtistResponse createdResponse = ArtistResponse.builder()
                .id(2L)
                .name("Queen")
                .isSinger(false)
                .isBand(true)
                .build();

        when(artistService.create(any(ArtistCreateRequest.class))).thenReturn(createdResponse);

        mockMvc.perform(post("/api/v1/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Queen"));
    }

    @Test
    void update_WithExistingId_ReturnsUpdatedArtist() throws Exception {
        ArtistUpdateRequest request = new ArtistUpdateRequest();
        request.setName("The Beatles (Updated)");

        ArtistResponse updatedResponse = ArtistResponse.builder()
                .id(1L)
                .name("The Beatles (Updated)")
                .isSinger(false)
                .isBand(true)
                .build();

        when(artistService.update(eq(1L), any(ArtistUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/artists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("The Beatles (Updated)"));
    }

    @Test
    void update_WithNonExistingId_ReturnsNotFound() throws Exception {
        ArtistUpdateRequest request = new ArtistUpdateRequest();
        request.setName("Updated Name");

        when(artistService.update(eq(999L), any(ArtistUpdateRequest.class)))
                .thenThrow(new EntityNotFoundException("Artist not found"));

        mockMvc.perform(put("/api/v1/artists/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
