package com.re.artworkgallery_security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkDTO {
    private Long id;
    private String title;
    private String description;
    private Boolean isPublished;
    private Long ownerId;
}
