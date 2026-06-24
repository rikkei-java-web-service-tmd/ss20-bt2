package com.re.artworkgallery_security.controller;

import com.re.artworkgallery_security.dto.ArtworkDTO;
import com.re.artworkgallery_security.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gallery/artworks")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;

    @GetMapping
    public ResponseEntity<List<ArtworkDTO>> getArtworks() {
        List<ArtworkDTO> artworks = artworkService.getArtworks();
        return ResponseEntity.ok(artworks);
    }
}
