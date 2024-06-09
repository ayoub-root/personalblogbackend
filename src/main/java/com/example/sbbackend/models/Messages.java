package com.example.sbbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Messages {
    @Id
    @GeneratedValue
    private  Long id;
    private String name;
    private String email;
    private  String subject;
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Content is required")
    private String content;
    private  Boolean isRead;
    @ManyToOne
    private User createdBy;
    private Date createdAt;
    private Date updatedAt;
}
