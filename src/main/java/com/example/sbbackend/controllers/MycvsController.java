package com.example.sbbackend.controllers;

import com.example.sbbackend.dto.Role;
import com.example.sbbackend.helpers.FileService;
import com.example.sbbackend.models.MyCvs;
import com.example.sbbackend.models.User;
import com.example.sbbackend.services.CommentsService;
import com.example.sbbackend.services.MyCvsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Objects;

import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mycvs")

public class MycvsController {

    private final MyCvsService myCvsService;
    private final CommentsService commentsService;
    private final FileService fileService;

    @GetMapping
    public ResponseEntity<Page<?>> getAll(@PageableDefault(
            page = 0,
            size = 10,
            sort = {"createdAt"},
            direction = Direction.DESC
    )
                                          Pageable pageable) {
        Page<?> myCvsPage = myCvsService.getAll(pageable);
        return ResponseEntity.

                status(200).

                body(myCvsPage);
    }


    @GetMapping("/{myCvTitle}")
    public ResponseEntity<?> getMyCvBySlug(@PathVariable String myCvTitle) {

        MyCvs myCv = myCvsService.getByTitle(myCvTitle);

        if (myCv == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(myCv);
    }

    @PostMapping("/")
    public ResponseEntity<?> createNewMyCv(@Valid
                                           @ModelAttribute MyCvs request,
                                           @RequestParam(name = "photo", required = false) MultipartFile photo, @RequestParam(name = "file", required = false) MultipartFile file

    ) {
        if (file != null) {
            System.out.println(file.getOriginalFilename());
            String newName = fileService.save(file, "mycvs");
            request.setFileUrl(newName);
        }
        if (photo != null) {
            System.out.println(photo.getOriginalFilename());
            String newName = fileService.save(photo, "cvphoto");
            request.setPhotoUrl(newName);
        }
        try {
            var data = myCvsService.createNewMyCv(request);
            return ResponseEntity.status(200).body(data);
        } catch (Error err) {
            return ResponseEntity.status(401).body(err);
        }

    }

    @PutMapping("/{myCvId}")
    public ResponseEntity<?> updateMyCv(@PathVariable Long myCvId,
                                        @Valid @ModelAttribute MyCvs request,
                                        @RequestParam(name = "file", required = false) MultipartFile file,
                                        @RequestParam(name = "photo", required = false) MultipartFile photo) {

        User auth = getAuthenticatedUser();
        MyCvs myCv = myCvsService.getById(myCvId);
        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN || Objects.equals(myCv.getCreatedBy().getId(), auth.getId())) {
            if (file != null) {
                System.out.println(file.getOriginalFilename());
                String newName = fileService.save(file, "mycvs");
                request.setFileUrl(newName);
            } else {
                request.setFileUrl(null);
            }
            if (photo != null) {
                System.out.println(photo.getOriginalFilename());
                String newName = fileService.save(photo, "cvphoto");
                request.setPhotoUrl(newName);
            } else {
                request.setPhotoUrl(null);
            }

            try {

                return ResponseEntity.ok(myCvsService.updateMyCv(myCvId, request));
            } catch (Error err) {
                return ResponseEntity.status(401).body(err);
            }
        } else
            return ResponseEntity.status(403).body("you are not admin");
    }


    @DeleteMapping("/{myCvId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long myCvId) {

        User auth = getAuthenticatedUser();
        MyCvs myCv = myCvsService.getById(myCvId);
        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN || Objects.equals(myCv.getCreatedBy().getId(), auth.getId())) {
            myCvsService.deleteMyCv(myCvId);

            return ResponseEntity.status(200).body("myCv is deleted successfully");
        } else
            return ResponseEntity.status(403).body("you are not admin");

    }

}

