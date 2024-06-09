package com.example.sbbackend.controllers;

import com.example.sbbackend.dto.AuthRequestDto;
import com.example.sbbackend.dto.AuthResponse;
import com.example.sbbackend.dto.RegisterRequestDto;
import com.example.sbbackend.dto.UpdatePasswordRequestDto;
import com.example.sbbackend.helpers.FileService;
import com.example.sbbackend.models.User;
import com.example.sbbackend.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final FileService fileService;
    private final AuthenticationService service;
    private final HttpServletResponse httpServletResponse;
    private final AuthenticationService authenticationService;

    @PostMapping("/register/")
    public ResponseEntity<?> register(
            @ModelAttribute RegisterRequestDto request,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        if (file != null) {
            System.out.println(file.getOriginalFilename());

            String newName = fileService.save(file, "profiles");
            request.setAvatar(newName);
        }

        return ResponseEntity.status(200).body(service.register(request));
    }
    @GetMapping("/confirm")
    public ResponseEntity<?> confirmAccount(@RequestParam("token") String token) {
        return authenticationService.confirmToken(token);
    }
    @PostMapping("/addaccount")
    public ResponseEntity<?> addAccount(
            @ModelAttribute RegisterRequestDto request,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        if (file != null) {
            System.out.println(file.getOriginalFilename());

            String newName = fileService.save(file, "profiles");
            request.setAvatar(newName);
        }

        return ResponseEntity.status(200).body(service.registerAdmin(request));
    }

    @PutMapping("/{email}")
    public ResponseEntity<?> updateUser(@PathVariable String email,
                                        @Valid @ModelAttribute User request,
                                        @RequestParam(name = "file", required = false) MultipartFile file) {


        if (file != null) {
            // System.out.println(file.getOriginalFilename());
            String newName = fileService.save(file, "profiles");
            request.setAvatar(newName);
        } else {
            request.setAvatar(null);
        }

        try {
            var data = authenticationService.updateExistingUser(email, request);
            return ResponseEntity.status(data.getStatusCode()).body(data);
        } catch (Error err) {
            return ResponseEntity.status(401).body(err);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthRequestDto request
           ) {
        System.out.println(request.getEmail());
        return service.authenticate(request);
    }


    @GetMapping("/logged")
    public ResponseEntity<?> isLogged(
            HttpServletRequest request,
            HttpServletResponse response) {
        return service.isAuthenticated(request, response);

    }



    @GetMapping("/test")
    public ResponseEntity<?> authenticate(
            @RequestParam String email, @RequestParam String password, HttpServletResponse response
    ) {
        LoggerFactory.getLogger(this.getClass()).warn(email);
        return  service.authenticate2(email, password);
    }

    @GetMapping("/")
    public ResponseEntity<String> home(
    ) {

        return ResponseEntity.ok("");

    }


}
