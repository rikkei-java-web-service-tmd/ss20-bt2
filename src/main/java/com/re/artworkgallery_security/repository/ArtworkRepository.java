package com.re.artworkgallery_security.repository;

import com.re.artworkgallery_security.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
}
