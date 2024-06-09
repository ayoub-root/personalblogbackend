package com.example.sbbackend.controllers;

import com.example.sbbackend.dto.*;
import com.example.sbbackend.helpers.FileService;
import com.example.sbbackend.models.Comments;
import com.example.sbbackend.models.Posts;
import com.example.sbbackend.models.User;
import com.example.sbbackend.repositories.UserRepository;
import com.example.sbbackend.services.CommentsService;
import com.example.sbbackend.services.PostsService;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")

public class PostsController {

    private final PostsService postsService;
    private final CommentsService commentsService;
    private final FileService fileService;
    private final UserRepository userRepository;

    @GetMapping("/all")
    public ResponseEntity<Page<?>> getAll(@PageableDefault(
            page = 0,
            size = 10,
            sort = {"createdAt"},
            direction = Direction.DESC
    )
                                          Pageable pageable) {

        Page<?> postsPage = postsService.getAll(pageable);
    return ResponseEntity.

                status(200).

                body(postsPage);


    }

    @GetMapping
    public ResponseEntity<Page<?>> getAllAdmin(@PageableDefault(
            page = 0,
            size = 10,
            sort = {"createdAt"},
            direction = Direction.DESC
    )
                                               Pageable pageable) {
        Page<?> postsPage = postsService.getAllPublishedPosts(pageable);
        return ResponseEntity.

                status(200).

                body(postsPage);
    }


    @PostMapping("/search")
    public ResponseEntity<List<?>> searchBlogPosts(@RequestBody SearchDto query) {
//System.out.println("eeeee  "+query.getTitle());
        return ResponseEntity.status(200).body(postsService.searchPosts(query.getTitle()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getPostBySlug(@PathVariable String slug) {
        if (slug.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
        }
        Optional<?> post = postsService.getBySlug(slug);

        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @PostMapping("/")
    public ResponseEntity<?> createNewPost(@Valid
                                           @ModelAttribute PostDto request,
                                           @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        if (file != null) {
            System.out.println(file.getOriginalFilename());
            String newName = fileService.save(file, "posts");
            request.setImage(newName);
        }
        try {
            var data = postsService.createNewPost(request);
            return ResponseEntity.status(data.getStatusCode()).body(data);
        } catch (Error err) {
            return ResponseEntity.status(401).body(err);
        }

    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable String postId,
                                        @Valid @ModelAttribute PostDto request,
                                        @RequestParam(name = "file", required = false) MultipartFile file) {

        User auth = getAuthenticatedUser();
        Posts post = postsService.getById(postId);
        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN || Objects.equals(post.getCreatedBy().getId(), auth.getId())) {
            if (file != null) {
                // System.out.println(file.getOriginalFilename());
                String newName = fileService.save(file, "posts");
                request.setImage(newName);
            } else {
                request.setImage(null);
            }

            try {
                var data = postsService.updateExistingPost(postId, request);
                return ResponseEntity.status(data.getStatusCode()).body(data);
            } catch (Error err) {
                return ResponseEntity.status(401).body(err);
            }
        } else
            return ResponseEntity.status(403).body("you are not admin");
    }

    @PostMapping("/{postId}/react")
    public ResponseEntity<?> reactToPost(
            @PathVariable String postId,
            @RequestBody ReactionsRequestDto request) {
        System.out.println(request.getType());
        PostCommentDto updatedPost = postsService.updateReaction(postId, request.getType());

        return ResponseEntity.status(200).body(updatedPost);
    }

    @PostMapping("/{postId}/comment/")
    public ResponseEntity<Comments> addNewComment(
            @PathVariable String postId,
            @ModelAttribute CommentDto request,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        Posts post = postsService.getById(postId);
        if (file != null) {
            System.out.println(file.getOriginalFilename());
            String newName = fileService.save(file, "comments");
            request.setImage(newName);
        }
        return ResponseEntity.ok(commentsService.createNewComment(post, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteMessage(@PathVariable String postId) {
        System.out.println(postId);
        User auth = getAuthenticatedUser();
        Posts post = postsService.getById(postId);
        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN || Objects.equals(post.getCreatedBy().getId(), auth.getId())) {
            postsService.deletePost(postId);

            return ResponseEntity.status(200).body("post is deleted successfully");
        } else
            return ResponseEntity.status(403).body("you are not admin");

    }

}

