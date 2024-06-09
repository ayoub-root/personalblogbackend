package com.example.sbbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Comments {
    @Id
    @GeneratedValue
    private Long id;
    @JsonIgnore
    @ManyToOne
    private Posts post;
    private String image;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;
    private Date createdAt;
    private Date updatedAt;


}
