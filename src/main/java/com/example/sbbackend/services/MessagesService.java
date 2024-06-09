package com.example.sbbackend.services;


import com.example.sbbackend.dto.Role;
import com.example.sbbackend.helpers.PostMapper;
import com.example.sbbackend.models.Messages;
import com.example.sbbackend.models.User;
import com.example.sbbackend.repositories.MessagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;

@Service
@RequiredArgsConstructor
public class MessagesService {


    private final MessagesRepository repository;
    @Autowired
    private WSNotificationService wsnservice;
    public Page<Messages> getAllMessages(Pageable pageable) {
        User auth = getAuthenticatedUser();
        if (Objects.requireNonNull(auth).getRole() == Role.ADMIN) {
        return repository.findAll(pageable);
        }
         else {

            return repository.findByEmail(auth.getEmail(), pageable);
        }
    }
    public Messages createNewMessage(Messages messageData) {
       // User user = getAuthenticatedUser();
        Messages newMessage = Messages.builder()

                .name(messageData.getName())
                .email(messageData.getEmail())
                .content(messageData.getContent())
                .subject(messageData.getSubject())
                .isRead(false)
                .createdBy(null)
                .createdAt(new Date())
                .updatedAt(null)
                .build();

        Messages saved= repository.save(newMessage);
     //   notificationsService.sendNotification(saved );
      return  saved;
    }


    public Optional<Messages> getMessage(Long id) {
      return   repository.findById(id);
    }
    public void deleteMessage(Long id) {
        repository.deleteById(id);
    }

}
