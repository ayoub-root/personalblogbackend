package com.example.sbbackend.models;

import com.example.sbbackend.dto.ReactionsType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Reactions {
    @Enumerated(EnumType.STRING)
    private ReactionsType type;  // e.g., "like", "love", "haha", "wow", etc.

    @ManyToOne
    private User user;

    private Date createdAt;
}
