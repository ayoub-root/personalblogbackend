package com.example.sbbackend.dto;

import com.example.sbbackend.models.User;
import jakarta.persistence.Embeddable;
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
public class ReactionsDto {
    private ReactionsType type;  // e.g., "like", "love", "haha", "wow", etc.
    private UserDto user;
    private Date createdAt;
}
