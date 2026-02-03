package br.com.seplag.musicapi.infrastructure.persistence.repository;

import br.com.seplag.musicapi.domain.model.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {
    Optional<Regional> findBySourceId(Long sourceId);
    boolean existsBySourceId(Long sourceId);
}
