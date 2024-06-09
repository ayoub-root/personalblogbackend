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

public class UsersListDto {

    private Long id;
    private String firstname;
    private String lastname;
    private String avatar;
    private String email;
    private Role role;
    private String birthday;
    private String address;
    private Boolean confirmed;
    private String passwordToken;
    private String confirmationToken;
    private String bio;
    private Date createdAt;
    private Date updatedAt;


}
