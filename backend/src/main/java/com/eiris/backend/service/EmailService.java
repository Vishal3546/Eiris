package com.eiris.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = "https://eiris.vercel.app/admin-reset-password.html?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Reset Request - Eiris Admin");
        message.setText("Hello,\n\n" +
                "You have requested to reset your admin password for Eiris.\n\n" +
                "Please click the link below to reset your password. This link is valid for 15 minutes.\n\n" +
                resetUrl + "\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Thanks,\nEiris Support Team");

        CompletableFuture.runAsync(() -> {
            try {
                mailSender.send(message);
                System.out.println("Password reset email sent successfully to " + to);
            } catch (Exception e) {
                System.err.println("Failed to send password reset email to " + to + ": " + e.getMessage());
                System.out.println("===== FALLBACK: PASSWORD RESET LINK =====");
                System.out.println("Reset URL for " + to + ": " + resetUrl);
                System.out.println("=========================================");
            }
        });
    }
}
