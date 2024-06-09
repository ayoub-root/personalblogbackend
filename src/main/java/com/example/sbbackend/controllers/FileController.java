package com.example.sbbackend.controllers;


import com.example.sbbackend.helpers.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping("/imagess/{folder}/{id}")
    public ResponseEntity<?> getImages(@PathVariable("id") String imageUri, @PathVariable("folder") String folder) {

        return ResponseEntity.status(200).body("<img src='" + fileService.load(imageUri, folder) + "' width='300px'/>");
    }
    @GetMapping("/myfiles/{folder}/{id}")
    public ResponseEntity<?> getFiles(@PathVariable("id") String imageUri, @PathVariable("folder") String folder) {

        return  fileService.loadfile(imageUri, folder);
    }




}
