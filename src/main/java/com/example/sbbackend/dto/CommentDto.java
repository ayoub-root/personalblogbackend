package com.example.sbbackend.dto;

import com.example.sbbackend.models.Posts;
import com.example.sbbackend.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {


    private String image;
    private String content;
    private UserDto createdBy;
    private Date createdAt;
    private Date updatedAt;
}
