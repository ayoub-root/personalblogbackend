package com.example.sbbackend.controllers;


import com.example.sbbackend.helpers.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestFileController {
    private final FileService fileService;

    @GetMapping("/images/{folder}/{id}")
    public Resource getImage(@PathVariable("folder") String folder, @PathVariable("id") String imageUri) {
        return fileService.load(imageUri, folder);
    }


}
