package br.com.seplag.musicapi.api.v1.controller;

import br.com.seplag.musicapi.api.v1.dto.album.AlbumCreateRequest;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumPageResponse;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumResponse;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumUpdateRequest;
import br.com.seplag.musicapi.application.service.AlbumService;
import br.com.seplag.musicapi.application.usecase.CreateAlbumUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {

    @Mock
    private AlbumService albumService;

    @Mock
    private CreateAlbumUseCase createAlbumUseCase;

    @InjectMocks
    private AlbumController albumController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AlbumResponse testAlbumResponse;
    private AlbumPageResponse testPageResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testAlbumResponse = AlbumResponse.builder()
                .id(1L)
                .title("Abbey Road")
                .releaseYear(1969)
                .genre("Rock")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testPageResponse = AlbumPageResponse.builder()
                .content(List.of(testAlbumResponse))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
    }

    @Test
    void findAll_ReturnsPageOfAlbums() throws Exception {
        when(albumService.findAll(any(), any(), any(), any(Pageable.class))).thenReturn(testPageResponse);

        mockMvc.perform(get("/api/v1/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Abbey Road"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void findAll_WithFilters_ReturnsFilteredResults() throws Exception {
        when(albumService.findAll(eq("Abbey"), eq("Rock"), eq(1969), any(Pageable.class)))
                .thenReturn(testPageResponse);

        mockMvc.perform(get("/api/v1/albums")
                        .param("title", "Abbey")
                        .param("genre", "Rock")
                        .param("year", "1969"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Abbey Road"));
    }

    @Test
    void findAll_WithPagination_ReturnsPagedResults() throws Exception {
        when(albumService.findAll(any(), any(), any(), any(Pageable.class))).thenReturn(testPageResponse);

        mockMvc.perform(get("/api/v1/albums")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "title")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void findById_WithExistingId_ReturnsAlbum() throws Exception {
        when(albumService.findById(1L)).thenReturn(testAlbumResponse);

        mockMvc.perform(get("/api/v1/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Abbey Road"))
                .andExpect(jsonPath("$.releaseYear").value(1969));
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        when(albumService.findById(999L)).thenThrow(new EntityNotFoundException("Album not found"));

        mockMvc.perform(get("/api/v1/albums/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidRequest_ReturnsCreatedAlbum() throws Exception {
        AlbumCreateRequest request = new AlbumCreateRequest("Let It Be", 1970, "Rock", Set.of(1L));
        AlbumResponse createdResponse = AlbumResponse.builder()
                .id(2L)
                .title("Let It Be")
                .releaseYear(1970)
                .genre("Rock")
                .build();

        when(createAlbumUseCase.execute(any(AlbumCreateRequest.class))).thenReturn(createdResponse);

        mockMvc.perform(post("/api/v1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Let It Be"));
    }

    @Test
    void update_WithExistingId_ReturnsUpdatedAlbum() throws Exception {
        AlbumUpdateRequest request = new AlbumUpdateRequest();
        request.setTitle("Abbey Road (Remastered)");

        AlbumResponse updatedResponse = AlbumResponse.builder()
                .id(1L)
                .title("Abbey Road (Remastered)")
                .releaseYear(1969)
                .genre("Rock")
                .build();

        when(albumService.update(eq(1L), any(AlbumUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/albums/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Abbey Road (Remastered)"));
    }

    @Test
    void update_WithNonExistingId_ReturnsNotFound() throws Exception {
        AlbumUpdateRequest request = new AlbumUpdateRequest();
        request.setTitle("Updated Title");

        when(albumService.update(eq(999L), any(AlbumUpdateRequest.class)))
                .thenThrow(new EntityNotFoundException("Album not found"));

        mockMvc.perform(put("/api/v1/albums/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
