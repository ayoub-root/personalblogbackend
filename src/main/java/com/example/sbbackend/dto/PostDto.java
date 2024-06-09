package com.example.sbbackend.dto;

import com.example.sbbackend.models.Comments;
import com.example.sbbackend.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private String id;
    private String title;
    private String slug;
    private String domain;
    private String tags;
    private String image;
    private PostState state;

    private String readingTime;
    private String description;
    private String content;
    private UserDto createdBy;
    private Date createdAt;
    private Date updatedAt;
    private Integer comments;
    private List<ReactionsDto> reactions;
}
