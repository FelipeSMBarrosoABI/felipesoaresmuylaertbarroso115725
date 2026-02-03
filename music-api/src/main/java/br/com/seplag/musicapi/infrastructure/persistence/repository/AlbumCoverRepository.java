package br.com.seplag.musicapi.infrastructure.persistence.repository;

import br.com.seplag.musicapi.domain.model.AlbumCover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumCoverRepository extends JpaRepository<AlbumCover, Long> {
    Optional<AlbumCover> findByAlbumId(Long albumId);
    void deleteByAlbumId(Long albumId);
}
