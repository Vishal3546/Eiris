package com.eiris.backend;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TestBackend {
    public static void main(String[] args) throws Exception {
        String secret = "EirisSuperSecretKeyThatNeedsToBeVeryLongAndSecureEnoughForHS256Algorithm";
        
        // Generate JWT token for admin
        String token = Jwts.builder()
                .subject("admin@eiris.in")
                .claim("role", "ROLE_ADMIN")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
                
        System.out.println("Generated Token: " + token);
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/admin/orders"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
                
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }
}
