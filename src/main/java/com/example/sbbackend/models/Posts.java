package com.example.sbbackend.models;

import com.example.sbbackend.dto.PostState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Posts {
    @Id
    @UuidGenerator
    private String id;
    @NotBlank(message = "Title is required")
    @Column(unique = true)
    private String title;
    @Column(unique = true)
    private String slug;
    private String domain;
    private String tags;
    @Enumerated(EnumType.STRING)
    private PostState state;
    private String image;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Content is required")
    private String content;
    private String readingTime;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    private Date createdAt;
    private Date updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments;
    @ElementCollection
    @CollectionTable(name = "post_reactions", joinColumns = @JoinColumn(name = "post_id"))
    private List<Reactions> reactions;  // Embed the reactions as an element collection

}

