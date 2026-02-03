package br.com.seplag.musicapi.api.websocket.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumCreatedEvent {
    private Long albumId;
    private String albumTitle;
    private LocalDateTime createdAt;
}
