package br.com.seplag.musicapi.api.websocket;

import br.com.seplag.musicapi.api.websocket.payload.AlbumCreatedEvent;
import br.com.seplag.musicapi.domain.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumEventsPublisher {

    private static final String TOPIC_ALBUMS = "/topic/albums";

    private final SimpMessagingTemplate messagingTemplate;

    public void publishAlbumCreated(Album album) {
        AlbumCreatedEvent event = AlbumCreatedEvent.builder()
                .albumId(album.getId())
                .albumTitle(album.getTitle())
                .createdAt(album.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend(TOPIC_ALBUMS, event);
    }
}
