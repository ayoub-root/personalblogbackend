package com.example.sbbackend.dto;

import com.example.sbbackend.models.User;
import lombok.*;
import org.springframework.http.HttpHeaders;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AuthResponse {

    HttpHeaders headers;

    User user;
    String body;

}
