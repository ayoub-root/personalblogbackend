package com.example.sbbackend.services;

import com.example.sbbackend.dto.CommentDto;
import com.example.sbbackend.models.Comments;
import com.example.sbbackend.models.Posts;
import com.example.sbbackend.models.User;
import com.example.sbbackend.repositories.CommentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;

@Service
@RequiredArgsConstructor
public class CommentsService {


    private final CommentsRepository repository;

    public Comments createNewComment(Posts postId, CommentDto commentData) {
        User user = getAuthenticatedUser();
        Comments comment = Comments.builder()
                .post(postId)
                .content(commentData.getContent())
                .image(commentData.getImage())
                .createdBy(user)
                .createdAt(new Date())
                .updatedAt(null)
                .build();

        return repository.save(comment);
    }

    public Page<Comments> getAllComments(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void deleteComment(Long id) {
        repository.deleteById(id);
    }

}
