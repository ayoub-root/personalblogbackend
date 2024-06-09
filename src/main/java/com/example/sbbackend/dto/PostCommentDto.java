package com.example.sbbackend.dto;

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
public class PostCommentDto {
    private String id;
    private String title;
    private String slug;
    private String domain;
    private String tags;
    private PostState state;
    private String image;
    private String readingTime;
    private String description;
    private String content;
    private UserDto createdBy;
    private Date createdAt;
    private Date updatedAt;
    private List<CommentDto> comments;
    private List<SavedItemDto> savedItems;
    private List<ReactionsDto> reactions;
}
