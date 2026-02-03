package br.com.seplag.musicapi.infrastructure.persistence.specification;

import br.com.seplag.musicapi.domain.model.Album;
import org.springframework.data.jpa.domain.Specification;

public class AlbumSpecifications {

    public static Specification<Album> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Album> genreEquals(String genre) {
        return (root, query, cb) -> {
            if (genre == null || genre.isBlank()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("genre")), genre.toLowerCase());
        };
    }

    public static Specification<Album> releaseYearEquals(Integer year) {
        return (root, query, cb) -> {
            if (year == null) {
                return null;
            }
            return cb.equal(root.get("releaseYear"), year);
        };
    }

    public static Specification<Album> releaseYearBetween(Integer startYear, Integer endYear) {
        return (root, query, cb) -> {
            if (startYear == null && endYear == null) {
                return null;
            }
            if (startYear != null && endYear != null) {
                return cb.between(root.get("releaseYear"), startYear, endYear);
            }
            if (startYear != null) {
                return cb.greaterThanOrEqualTo(root.get("releaseYear"), startYear);
            }
            return cb.lessThanOrEqualTo(root.get("releaseYear"), endYear);
        };
    }
}
