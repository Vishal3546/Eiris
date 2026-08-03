package com.eiris.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private final Optional<JavaMailSender> mailSender;

    @Value("${spring.mail.username:anil9824530099@gmail.com}")
    private String fromEmail;

    @Value("${email.resend.api-key:}")
    private String resendApiKey;

    @Value("${email.resend.from:onboarding@resend.dev}")
    private String resendFromEmail;

    @Value("${email.brevo.api-key:}")
    private String brevoApiKey;

    @Value("${email.brevo.from:anil9824530099@gmail.com}")
    private String brevoFromEmail;

    @Autowired
    public EmailService(Optional<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = "https://eiris.vercel.app/admin-reset-password.html?token=" + token;

        System.out.println("===== PASSWORD RESET REQUEST =====");
        System.out.println("Recipient: " + to);
        System.out.println("Reset URL: " + resetUrl);
        System.out.println("==================================");

        CompletableFuture.runAsync(() -> {
            boolean sent = false;

            // 1. Try Resend REST API (HTTPS Port 443 - Never blocked on Render Free Tier)
            if (resendApiKey != null && !resendApiKey.trim().isEmpty()) {
                try {
                    sendViaResend(to, resetUrl);
                    sent = true;
                } catch (Exception e) {
                    System.err.println("❌ Resend API failed: " + e.getMessage());
                }
            }

            // 2. Try Brevo REST API (HTTPS Port 443) if Resend not configured or failed
            if (!sent && brevoApiKey != null && !brevoApiKey.trim().isEmpty()) {
                try {
                    sendViaBrevo(to, resetUrl);
                    sent = true;
                } catch (Exception e) {
                    System.err.println("❌ Brevo API failed: " + e.getMessage());
                }
            }

            // 3. Fallback to standard SMTP (JavaMailSender)
            if (!sent && mailSender.isPresent()) {
                try {
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

                    mailSender.get().send(message);
                    System.out.println("✅ Password reset email sent successfully via SMTP to " + to);
                    sent = true;
                } catch (Exception e) {
                    System.err.println("❌ SMTP Failed to send password reset email to " + to + ": " + e.getMessage());
                }
            }

            if (!sent) {
                System.out.println("===== FALLBACK: PASSWORD RESET LINK =====");
                System.out.println("Reset URL for " + to + ": " + resetUrl);
                System.out.println("=========================================");
            }
        });
    }

    private void sendViaResend(String to, String resetUrl) throws Exception {
        String jsonBody = """
                {
                  "from": "%s",
                  "to": ["%s"],
                  "subject": "Password Reset Request - Eiris Admin Portal",
                  "html": "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;'><h2 style='color: #0f172a;'>Reset Your Admin Password</h2><p style='color: #475569;'>You requested a password reset for the Eiris Admin Portal. Click the button below to set a new password:</p><div style='text-align: center; margin: 30px 0;'><a href='%s' style='background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;'>Reset Password</a></div><p style='color: #64748b; font-size: 13px;'>If the button doesn't work, copy and paste this link into your browser:<br><a href='%s'>%s</a></p><p style='color: #94a3b8; font-size: 12px; margin-top: 20px;'>This link is valid for 15 minutes. If you did not request this reset, please ignore this email.</p></div>"
                }
                """.formatted(resendFromEmail, to, resetUrl, resetUrl, resetUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("✅ Password reset email sent successfully via Resend API to: " + to);
        } else {
            throw new RuntimeException("Resend API failed with status " + response.statusCode() + ": " + response.body());
        }
    }

    private void sendViaBrevo(String to, String resetUrl) throws Exception {
        String jsonBody = """
                {
                  "sender": { "name": "Eiris Support", "email": "%s" },
                  "to": [ { "email": "%s" } ],
                  "subject": "Password Reset Request - Eiris Admin Portal",
                  "htmlContent": "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;'><h2 style='color: #0f172a;'>Reset Your Admin Password</h2><p style='color: #475569;'>You requested a password reset for the Eiris Admin Portal. Click the button below to set a new password:</p><div style='text-align: center; margin: 30px 0;'><a href='%s' style='background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;'>Reset Password</a></div><p style='color: #64748b; font-size: 13px;'>If the button doesn't work, copy and paste this link into your browser:<br><a href='%s'>%s</a></p><p style='color: #94a3b8; font-size: 12px; margin-top: 20px;'>This link is valid for 15 minutes. If you did not request this reset, please ignore this email.</p></div>"
                }
                """.formatted(brevoFromEmail, to, resetUrl, resetUrl, resetUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("api-key", brevoApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("✅ Password reset email sent successfully via Brevo API to: " + to);
        } else {
            throw new RuntimeException("Brevo API failed with status " + response.statusCode() + ": " + response.body());
        }
    }

    public void sendContactUsEmail(String name, String customerEmail, String subject, String messageContent) {
        System.out.println("===== CONTACT US FORM SUBMISSION =====");
        System.out.println("From: " + name + " (" + customerEmail + ")");
        System.out.println("Subject: " + subject);
        System.out.println("======================================");

        CompletableFuture.runAsync(() -> {
            boolean sent = false;
            String targetEmail = "anil9824530099@gmail.com"; // Admin notification email

            // 1. Try Resend REST API
            if (resendApiKey != null && !resendApiKey.trim().isEmpty()) {
                try {
                    String safeName = escapeJson(name);
                    String safeEmail = escapeJson(customerEmail);
                    String safeSubject = escapeJson(subject);
                    String safeMessage = escapeJson(messageContent);

                    String html = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;'><h2 style='color: #0f172a; border-bottom: 2px solid #e2e8f0; padding-bottom: 10px;'>New Contact Us Message - Eiris</h2><p style='color: #475569;'>You have received a new message from the Eiris Website Contact Us form:</p><table style='width: 100%%; border-collapse: collapse; margin-top: 15px;'><tr><td style='padding: 8px 0; font-weight: bold; color: #334155; width: 140px;'>Customer Name:</td><td style='color: #0f172a;'>%s</td></tr><tr><td style='padding: 8px 0; font-weight: bold; color: #334155;'>Customer Email:</td><td style='color: #2563eb;'><a href='mailto:%s'>%s</a></td></tr><tr><td style='padding: 8px 0; font-weight: bold; color: #334155;'>Subject:</td><td style='color: #0f172a;'>%s</td></tr></table><div style='margin-top: 20px; padding: 15px; background-color: #f8fafc; border-left: 4px solid #2563eb; border-radius: 4px;'><p style='margin: 0; color: #1e293b; white-space: pre-wrap;'>%s</p></div><p style='color: #94a3b8; font-size: 12px; margin-top: 25px;'>This message was sent via Eiris Website Contact Us Form.</p></div>"
                            .formatted(safeName, safeEmail, safeEmail, safeSubject, safeMessage);

                    String jsonBody = """
                            {
                              "from": "%s",
                              "to": ["%s"],
                              "subject": "New Contact Message: %s",
                              "html": "%s"
                            }
                            """.formatted(resendFromEmail, targetEmail, safeSubject, html);

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://api.resend.com/emails"))
                            .header("Authorization", "Bearer " + resendApiKey)
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                            .build();

                    HttpResponse<String> response = HttpClient.newHttpClient()
                            .send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        System.out.println("✅ Contact Us notification sent successfully via Resend API to: " + targetEmail);
                        sent = true;
                    } else {
                        System.err.println("❌ Resend API failed for Contact Us: " + response.statusCode() + " - " + response.body());
                    }
                } catch (Exception e) {
                    System.err.println("❌ Resend API failed for Contact Us: " + e.getMessage());
                }
            }

            // Fallback to standard SMTP
            if (!sent && mailSender.isPresent()) {
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(fromEmail);
                    message.setTo(targetEmail);
                    message.setSubject("New Contact Message: " + subject);
                    message.setText("From: " + name + " <" + customerEmail + ">\n\nSubject: " + subject + "\n\nMessage:\n" + messageContent);

                    mailSender.get().send(message);
                    System.out.println("✅ Contact Us notification sent successfully via SMTP to " + targetEmail);
                    sent = true;
                } catch (Exception e) {
                    System.err.println("❌ SMTP Failed to send contact email: " + e.getMessage());
                }
            }
        });
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "<br>");
    }
}


