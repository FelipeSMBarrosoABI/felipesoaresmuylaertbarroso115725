package br.com.seplag.musicapi.infrastructure.persistence.specification;

import br.com.seplag.musicapi.domain.model.Artist;
import org.springframework.data.jpa.domain.Specification;

public class ArtistSpecifications {

    public static Specification<Artist> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Artist> isSinger(Boolean isSinger) {
        return (root, query, cb) -> {
            if (isSinger == null) {
                return null;
            }
            return cb.equal(root.get("isSinger"), isSinger);
        };
    }

    public static Specification<Artist> isBand(Boolean isBand) {
        return (root, query, cb) -> {
            if (isBand == null) {
                return null;
            }
            return cb.equal(root.get("isBand"), isBand);
        };
    }
}
