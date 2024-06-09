package com.example.sbbackend.repositories;

 import com.example.sbbackend.models.Filters;
 import com.example.sbbackend.models.MyCvs;
 import com.example.sbbackend.models.Posts;
 import com.example.sbbackend.models.User;
 import org.springframework.data.jpa.repository.JpaRepository;

 import java.util.List;
 import java.util.Optional;

public interface MyCvsRepository extends JpaRepository<MyCvs, Long> {
 Optional<MyCvs> findByTitle(String title);
}
