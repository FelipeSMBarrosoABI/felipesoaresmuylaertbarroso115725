package br.com.seplag.musicapi.application.usecase;

import br.com.seplag.musicapi.api.v1.dto.album.AlbumCreateRequest;
import br.com.seplag.musicapi.api.v1.dto.album.AlbumResponse;
import br.com.seplag.musicapi.api.v1.mapper.AlbumMapper;
import br.com.seplag.musicapi.api.websocket.AlbumEventsPublisher;
import br.com.seplag.musicapi.application.service.AlbumService;
import br.com.seplag.musicapi.domain.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateAlbumUseCase {

    private final AlbumService albumService;
    private final AlbumMapper albumMapper;
    private final AlbumEventsPublisher albumEventsPublisher;

    @Transactional
    public AlbumResponse execute(AlbumCreateRequest request) {
        Album album = albumService.create(request);

        // Publish WebSocket event
        albumEventsPublisher.publishAlbumCreated(album);

        return albumMapper.toResponseWithoutCover(album);
    }
}
