package com.example.sbbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyCvsDto {

    private Long id;
    private String title;
    private String language;
    private String content;
    private String fileUrl;
    private String photoUrl;

    private UserDto createdBy;
    private Date createdAt;
    private Date updatedAt;


}
