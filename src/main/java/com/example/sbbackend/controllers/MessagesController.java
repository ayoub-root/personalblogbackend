package com.example.sbbackend.controllers;


import com.example.sbbackend.dto.Role;
import com.example.sbbackend.models.Messages;
import com.example.sbbackend.models.User;
import com.example.sbbackend.services.MessagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

import static com.example.sbbackend.services.AuthenticationService.getAuthenticatedUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessagesController {
    private final MessagesService messagesService;

    @GetMapping
    public ResponseEntity<Page<?>> getAllMessages(@PageableDefault(
            page = 0,
            size = 10,
            sort = {"createdAt"},
            direction = Sort.Direction.DESC
    )
                                                  Pageable pageable) {
        Page<?> messagesPage = messagesService.getAllMessages(pageable);
        return ResponseEntity.
                status(200).
                body(messagesPage);
    }

    @PostMapping("/")
    public ResponseEntity<?> CreateNwmessage(@RequestBody Messages messageRequest) {

        messagesService.createNewMessage(messageRequest);
        return ResponseEntity.status(200).body("message is created successfully");
    }

    @DeleteMapping("/{messageid}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageid) {
        System.out.println(messageid);
        User auth = getAuthenticatedUser();
        Optional<Messages> message = messagesService.getMessage(messageid);
        if (message.isPresent()) {
            if (Objects.requireNonNull(auth).getRole() == Role.ADMIN || Objects.equals(message.get().getEmail(), auth.getEmail())) {

                messagesService.deleteMessage(messageid);
                return ResponseEntity.status(200).body("message is deleted successfully");
            } else {
                return ResponseEntity.status(403).body("you are not admin");
            }

        } else {
            return ResponseEntity.status(404).body("message not found  ");
        }

    }
}