package com.example.sbbackend.services;

import com.example.sbbackend.dto.CommentDto;
import com.example.sbbackend.helpers.DuplicateEntryException;
import com.example.sbbackend.helpers.ForbiddenException;
import com.example.sbbackend.helpers.NetworkException;
import com.example.sbbackend.models.Comments;
import com.example.sbbackend.models.Filters;
import com.example.sbbackend.models.Posts;
import com.example.sbbackend.models.User;
import com.example.sbbackend.repositories.CommentsRepository;
import com.example.sbbackend.repositories.FiltersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;

@Service
@RequiredArgsConstructor
public class FiltersService {


    private final FiltersRepository repository;
    public List<Filters> getAllFilters( ) {
        return repository.findAll();
    }
    public Optional<?> createNewFilter(Filters filterData) {
        User user = getAuthenticatedUser();
        Filters newFilter = Filters.builder()
                .label(filterData.getLabel())
                .type(filterData.getType())
                .createdBy(user)
                .createdAt(new Date())
                .updatedAt(null)
                .build();
        try {
            return Optional.of(repository.save(newFilter));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEntryException("Duplicate entry for label and type.");
        } catch (NetworkException ex) { // Replace with actual network exception
            throw new NetworkException("Network error occurred.");
        } catch (ForbiddenException ex) { // Replace with actual forbidden access exception
            throw new ForbiddenException("You do not have permission to perform this action.");
        } catch (Exception ex) {
            throw new RuntimeException("An unexpected error occurred.");
        }
    }



    public void deleteFilter(Long id) {
        repository.deleteById(id);
    }

}
