package com.example.sbbackend.services;

import com.example.sbbackend.dto.MyCvsDto;
import com.example.sbbackend.dto.Role;
import com.example.sbbackend.helpers.*;
import com.example.sbbackend.models.MyCvs;
import com.example.sbbackend.models.Posts;
import com.example.sbbackend.models.User;
import com.example.sbbackend.repositories.MyCvsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;

@Service
@RequiredArgsConstructor
public class MyCvsService {


    private final MyCvsRepository repository;
    private final FileService fileService;

    public Page<MyCvsDto> getAll(Pageable pageable) {
        Page<MyCvs> mycvs = repository.findAll(pageable);
        return mycvs.map(PostMapper::toMyCvsDto);
    }

    public Optional<?> createNewMyCv(MyCvs myCvData) {
        User user = getAuthenticatedUser();
        MyCvs newMyCv = MyCvs.builder()
                .title(myCvData.getTitle())
                .language(myCvData.getLanguage())
                .content(myCvData.getContent())
                .createdBy(user)
                .fileUrl(myCvData.getFileUrl())
                .photoUrl(myCvData.getPhotoUrl())
                .createdAt(new Date())
                .updatedAt(null)
                .build();
        try {
            return Optional.of(repository.save(newMyCv));
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
    public MyCvs getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "mycv not found"));
    }
    public MyCvs getByTitle(String title) {
        return repository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "mycv not found"));
    }
    public ResponseEntity<?> updateMyCv(Long myCvId, MyCvs myCvData) {
        MyCvs mycv = repository.findById(myCvId).orElse(null);

        User auth = getAuthenticatedUser();
        if (auth == null || mycv == null) return null;

        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN
                ||
                Objects.equals(Objects.requireNonNull(auth).getId(), Objects.requireNonNull(mycv).getCreatedBy().getId())) {

            mycv.setTitle(myCvData.getTitle());
            mycv.setLanguage(myCvData.getLanguage());
            mycv.setContent(myCvData.getContent());
            if(myCvData.getFileUrl()!=null)
            {

                mycv.setFileUrl(myCvData.getFileUrl());
            }
            if(myCvData.getPhotoUrl()!=null)
            {

                mycv.setPhotoUrl(myCvData.getPhotoUrl());
            }
            mycv.setUpdatedAt(new Date());
            System.out.println(mycv.getTitle());
            try {
                repository.save(mycv);
             return ResponseEntity.status(200).body("updated");

            } catch (DataIntegrityViolationException ex) {
                throw new DuplicateEntryException("Duplicate entry for label and type.");
            } catch (NetworkException ex) { // Replace with actual network exception
                throw new NetworkException("Network error occurred.");
            } catch (ForbiddenException ex) { // Replace with actual forbidden access exception
                throw new ForbiddenException("You do not have permission to perform this action.");
            } catch (Exception ex) {
                throw new RuntimeException("An unexpected error occurred.");
            }
        } else return ResponseEntity.status(403).body("erooor");

    }


    public void deleteMyCv(Long id) {
        MyCvs cv = repository.findById(id).orElseThrow(() -> new ForbiddenException(" cv not found"));

        if (cv.getFileUrl() != null) {
           String foldercv = "mycvs"; // or whatever folder you use to store user photos
            boolean deleted1 = fileService.delete(cv.getFileUrl(), foldercv);


        }

        if (cv.getPhotoUrl() != null) {
            String folderavatar = "cvphoto"; // or whatever folder you use to store user photos
            boolean deleted2 = fileService.delete(cv.getPhotoUrl(), folderavatar);
        }
            repository.deleteById(id);
    }

}
