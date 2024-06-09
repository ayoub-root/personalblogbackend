package com.example.sbbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserDto {


    private String firstname;
    private String lastname;
    private String avatar;
    //private  Role  role;
}
