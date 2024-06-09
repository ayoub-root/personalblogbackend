package com.example.sbbackend.services;

import com.example.sbbackend.dto.*;
import com.example.sbbackend.helpers.FileService;
import com.example.sbbackend.helpers.ForbiddenException;
import com.example.sbbackend.helpers.PostMapper;
import com.example.sbbackend.helpers.Utils;
import com.example.sbbackend.models.Posts;
import com.example.sbbackend.models.Reactions;
import com.example.sbbackend.models.User;
import com.example.sbbackend.repositories.PostsRepository;
import com.example.sbbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.sbbackend.helpers.PostMapper.toPostDto;
import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;

@Service
@RequiredArgsConstructor
public class PostsService {
    @Autowired
    private WSNotificationService wsnservice;

    private final PostsRepository repository;
    @Autowired
    private FileService fileService;

    public ResponseEntity<?> createNewPost(PostDto postData) {

        if (postData.getTitle() == null
                || postData.getContent() == null
                || postData.getContent().equals("null")
                || postData.getTitle().equals("null")

                || postData.getTitle().isEmpty()
                || postData.getContent().isEmpty())
            return ResponseEntity.status(401).body("Title and content are required");

        User user = getAuthenticatedUser();

        Posts post = Posts.builder()

                .title(postData.getTitle())
                .content(postData.getContent())
                .slug(Utils.toSlug(postData.getTitle()))
                .domain(postData.getDomain())
                .tags(postData.getTags())
                .state(postData.getState())
                .image(postData.getImage())
                .readingTime(postData.getReadingTime())
                .createdBy(user)
                .description(postData.getDescription())
                .createdAt(new Date())
                .updatedAt(null)
                .build();

        repository.save(post);
        wsnservice.pushNotification("new post: '"+post.getTitle()+"' ,plz refresh to get updates. ");

        return ResponseEntity.status(200).body("post created");
    }

    public ResponseEntity<?> updateExistingPost(String postId, PostDto postData) {

        if (postData.getTitle() == null
                || postData.getContent() == null
                || postData.getContent().equals("null")
                || postData.getTitle().equals("null")
                || postData.getTitle().isEmpty()
                || postData.getContent().isEmpty()) {
            return ResponseEntity.status(401).body("Title and content are required");
        }

        Posts post = repository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(404).body("Post not found");
        }

        post.setTitle(postData.getTitle());
        post.setContent(postData.getContent());
        post.setSlug(Utils.toSlug(postData.getTitle()));
        post.setDomain(postData.getDomain());
        post.setTags(postData.getTags());
        if(postData.getImage()!=null)
        {
            post.setImage(postData.getImage());
        }

        post.setState(postData.getState());

        post.setReadingTime(postData.getReadingTime());
        post.setDescription(postData.getDescription());
        post.setUpdatedAt(new Date());

        repository.save(post);
        wsnservice.pushNotification("Post updated: '" + post.getTitle() + "' ,please refresh to get updates.");

        return ResponseEntity.status(200).body("Post updated");
    }

    public Page<PostDto> getAll(Pageable pageable) {
User user= getAuthenticatedUser();
        Page<Posts> posts = repository.findAllByStateOrCreatedBy(PostState.PUBLISHED,user, pageable);
        return posts.map(PostMapper::toALLPostDto);
    }

    public Page<PostDto> getAllPublishedPosts(Pageable pageable) {
        Page<Posts> posts = repository.findAllByState(PostState.PUBLISHED, pageable);
        return posts.map(PostMapper::toALLPostDto);
    }

    public List<SearchDto> searchPosts(String query) {
        // Stream over the repository findAll method
        var list = repository.findAll().stream()
                .filter(post -> post.getTitle().toLowerCase().contains(query.toLowerCase()))
                .toList();

        // Map the filtered list to SavedItemDto using PostMapper
        return list.stream()
                .map(PostMapper::toSearchItemDto)
                .collect(Collectors.toList());
    }
    public Optional<?> getBySlug(String slug) {
        return Optional.of(toPostDto(repository.findBySlug(slug)));


        //  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    public Posts getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    public PostCommentDto updateReaction(String postId, ReactionsType reactionType) {
        Optional<Posts> optionalPost = repository.findById(postId);
        if (optionalPost.isEmpty()) {
            return null;
        }

        User user = getAuthenticatedUser();
        if (user == null) {
            return null;
        }

        Posts post = optionalPost.get();

        System.out.println(post.getCreatedBy().getId() + "  #####  " + user.getId());
        List<Reactions> reactions = post.getReactions();
        Optional<Reactions> existingReactionOpt = reactions.stream()
                .filter(reaction -> reaction.getUser().getId().equals(user.getId()))
                .findFirst();

        if (existingReactionOpt.isPresent()) {
            Reactions existingReaction = existingReactionOpt.get();
            if (existingReaction.getType().equals(reactionType)) {
                // Remove reaction if it's the same type
                reactions.remove(existingReaction);
            } else {
                // Update reaction type if it's different
                existingReaction.setType(reactionType);
                existingReaction.setCreatedAt(new Date());
            }
        } else {
            // Add new reaction
            Reactions newReaction = Reactions.builder()
                    .type(reactionType)
                    .user(user)
                    .createdAt(new Date())
                    .build();
            reactions.add(newReaction);
        }
        System.out.println(reactions.size());
        post.setReactions(reactions);
        Posts aa = repository.save(post);
        return toPostDto(aa);
    }
    public void deletePost(String id) {
        Posts post = repository.findById(id).orElseThrow(() -> new ForbiddenException(" user not found"));

        if (post.getImage() != null) {
            String folder = "posts"; // or whatever folder you use to store user photos
            boolean deleted = fileService.delete(post.getImage(), folder);
            if (!deleted) {
                // Handle the case where the photo could not be deleted
                // For example, log an error or throw an exception
                System.err.println("Failed to delete photo: " + post.getImage());
            }
        }
        repository.deleteById(id);
    }
}
