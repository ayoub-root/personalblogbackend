package com.example.sbbackend.repositories;

import com.example.sbbackend.dto.PostState;
import com.example.sbbackend.models.Posts;
import com.example.sbbackend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, String> {

    Posts findBySlug(String slug);

    Optional<List<Posts>> findAllByCreatedBy(User createdBy);

    Optional<List<Posts>> findAllByDomain(String domain);
    Page<Posts> findAllByState(PostState state, Pageable pageable);
    void deleteBySlug(String slug);
    Page<Posts> findAllByStateOrCreatedBy(PostState state, User createdBy, Pageable pageable);
    Page<Posts> findAllByCreatedBy_Id( Long id, Pageable pageable);

}
