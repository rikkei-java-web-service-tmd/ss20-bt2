package com.re.artworkgallery_security.service;

import com.re.artworkgallery_security.dto.ArtworkDTO;
import com.re.artworkgallery_security.model.Artwork;
import com.re.artworkgallery_security.repository.ArtworkRepository;
import com.re.artworkgallery_security.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtworkService {

    @Autowired
    private ArtworkRepository artworkRepository;

    public List<ArtworkDTO> getArtworks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        boolean isAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
                
        boolean isArtist = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ARTIST"));

        Long currentUserId = userDetails.getId();

        List<Artwork> allArtworks = artworkRepository.findAll();

        return allArtworks.stream()
                .filter(artwork -> {
                    if (isAdmin) {
                        return true;
                    } else if (isArtist) {
                        return artwork.getIsPublished() || artwork.getOwnerId().equals(currentUserId);
                    }
                    return artwork.getIsPublished();
                })
                .map(artwork -> new ArtworkDTO(
                        artwork.getId(),
                        artwork.getTitle(),
                        artwork.getDescription(),
                        artwork.getIsPublished(),
                        artwork.getOwnerId()
                ))
                .collect(Collectors.toList());
    }
}
