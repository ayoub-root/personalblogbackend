package com.example.sbbackend.repositories;

 import com.example.sbbackend.models.Filters;
 import com.example.sbbackend.models.Messages;
 import com.example.sbbackend.models.User;
 import org.springframework.data.domain.Page;
 import org.springframework.data.domain.Pageable;
 import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Messages, Long> {
 Page<Messages> findByEmail(String email, Pageable pageable);
}
