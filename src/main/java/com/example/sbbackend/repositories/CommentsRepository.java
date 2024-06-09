package com.example.sbbackend.repositories;

import com.example.sbbackend.models.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
}
