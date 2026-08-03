package com.eiris.backend.controller;

import com.eiris.backend.dto.request.ContactRequest;
import com.eiris.backend.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> sendMessage(@Valid @RequestBody ContactRequest request) {
        emailService.sendContactUsEmail(request.name(), request.email(), request.subject(), request.message());
        return ResponseEntity.ok("Your message has been sent successfully!");
    }
}
