package com.example.sbbackend.dto;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedItemDto {

    private String id;
    private String title;
    private String type;
    private String image;
    private String value;
}
