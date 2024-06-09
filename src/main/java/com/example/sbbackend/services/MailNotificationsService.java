package com.example.sbbackend.services;

import com.example.sbbackend.models.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MailNotificationsService {

@Autowired
    private   JavaMailSender mailSender;

    public ResponseEntity<?> sendNotification( String from, String to, String subject,String body) {
System.out.println(from+" \n"+to+"\n"+subject+"\n"+body);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(to); // Replace with your admin email
        email.setSubject(subject);
     email.setText(body);
     try {
         mailSender.send(email);
         return ResponseEntity.status(200).body("email is sent") ;
     } catch (MailSendException e) {
        // Handle failed send attempts
        System.err.println("Failed to send email: " + e.getMessage());
      return ResponseEntity.status(401).body(e.getMessage()) ;
        // Add appropriate handling or rethrow the exception
    } catch (MailAuthenticationException e) {
        // Handle authentication issues
        System.err.println("Authentication failed: " + e.getMessage());
         return ResponseEntity.status(401).body(e.getMessage()) ;
        // Add appropriate handling or rethrow the exception
    } catch (MailParseException e) {
        // Handle parsing issues
        System.err.println("Failed to parse email: " + e.getMessage());
         return ResponseEntity.status(401).body(e.getMessage()) ;
        // Add appropriate handling or rethrow the exception
    } catch (MailPreparationException e) {
        // Handle preparation issues
        System.err.println("Failed to prepare email: " + e.getMessage());
         return ResponseEntity.status(401).body(e.getMessage()) ;
        // Add appropriate handling or rethrow the exception
    } catch (MailException e) {
        // General mail exception
        System.err.println("MailException: " + e.getMessage());
         return ResponseEntity.status(401).body(e.getMessage()) ;
        // Add appropriate handling or rethrow the exception
    }

    }
}
