package br.com.seplag.musicapi.application.service;

import br.com.seplag.musicapi.api.v1.dto.cover.CoverResponse;
import br.com.seplag.musicapi.api.v1.dto.cover.UploadCoverResponse;
import br.com.seplag.musicapi.domain.model.Album;
import br.com.seplag.musicapi.domain.model.AlbumCover;
import br.com.seplag.musicapi.infrastructure.persistence.repository.AlbumCoverRepository;
import br.com.seplag.musicapi.infrastructure.persistence.repository.AlbumRepository;
import br.com.seplag.musicapi.infrastructure.storage.s3.MinioStorageService;
import br.com.seplag.musicapi.infrastructure.storage.s3.PresignedUrlService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlbumCoverService {

    private final AlbumRepository albumRepository;
    private final AlbumCoverRepository albumCoverRepository;
    private final MinioStorageService minioStorageService;
    private final PresignedUrlService presignedUrlService;

    @Transactional
    public UploadCoverResponse uploadCover(Long albumId, MultipartFile file) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id: " + albumId));

        // Delete existing cover if present
        Optional<AlbumCover> existingCover = albumCoverRepository.findByAlbumId(albumId);
        if (existingCover.isPresent()) {
            minioStorageService.delete(existingCover.get().getObjectKey());
            albumCoverRepository.delete(existingCover.get());
        }

        // Upload new cover
        String objectKey = minioStorageService.upload(file, "covers/album-" + albumId);

        // Save cover metadata
        AlbumCover cover = AlbumCover.builder()
                .album(album)
                .objectKey(objectKey)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .build();

        albumCoverRepository.save(cover);

        String presignedUrl = presignedUrlService.generatePresignedUrl(objectKey);

        return UploadCoverResponse.builder()
                .albumId(albumId)
                .objectKey(objectKey)
                .presignedUrl(presignedUrl)
                .build();
    }

    @Transactional(readOnly = true)
    public CoverResponse getCover(Long albumId) {
        AlbumCover cover = albumCoverRepository.findByAlbumId(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Cover not found for album: " + albumId));

        String presignedUrl = presignedUrlService.generatePresignedUrl(cover.getObjectKey());

        return CoverResponse.builder()
                .albumId(albumId)
                .presignedUrl(presignedUrl)
                .contentType(cover.getContentType())
                .fileSize(cover.getFileSize())
                .build();
    }

    @Transactional
    public void deleteCover(Long albumId) {
        AlbumCover cover = albumCoverRepository.findByAlbumId(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Cover not found for album: " + albumId));

        minioStorageService.delete(cover.getObjectKey());
        albumCoverRepository.delete(cover);
    }
}
