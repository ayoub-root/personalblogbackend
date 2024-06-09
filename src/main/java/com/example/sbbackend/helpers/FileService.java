package com.example.sbbackend.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final Path root = Paths.get("uploads");
    String rootFolder = "uploads";

    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException ex) {
            throw new RuntimeException("Could not initialize root folder");
        }
    }

    public String save(MultipartFile file, String folder) {
        if (folder != null) {
            try {
                Files.createDirectories(Paths.get(rootFolder + "/" + folder));
            } catch (IOException ex) {
                throw new RuntimeException("Could not create '" + folder + "' folder");
            }
        }
        Path currentFolder = Paths.get(rootFolder + "/" + folder);
        try {
            // Generate timestamp
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = now.format(formatter);

            // Generate random name
            String randomName = UUID.randomUUID().toString();

            // Extracting original file extension
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Combine timestamp, random name, and original file extension
            String newFilename = timestamp + "_" + randomName + fileExtension;

            Files.copy(file.getInputStream(), currentFolder.resolve(newFilename));
            return newFilename;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public Resource load(String filename, String folder) {
        if (filename == null) return null;
        try {
            Path currentFolder = Paths.get(rootFolder + "/" + folder);
            Path file = currentFolder.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException mex) {
            throw new RuntimeException("Error: " + mex.getMessage());
        }
    }

    public ResponseEntity<Resource> loadfile(String filename, String folder) {
        try {
            Path currentFolder = Paths.get(rootFolder + "/" + folder);
            Path file = currentFolder.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file.normalize());
                if (contentType == null) {
                    // Fallback to a default content type if none is found
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to delete a file
    public boolean delete(String filename, String folder) {
        if (filename == null) return false;
        try {
            Path currentFolder = Paths.get(rootFolder + "/" + folder);
            Path file = currentFolder.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
