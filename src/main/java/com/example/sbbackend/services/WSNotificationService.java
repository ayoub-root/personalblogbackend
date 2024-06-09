package com.example.sbbackend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WSNotificationService   {

    @Autowired
    private  SimpMessagingTemplate messagingTemplate;




    public void pushNotification(String message) {
        messagingTemplate.convertAndSend("/topic/notification", message);
    }
}
