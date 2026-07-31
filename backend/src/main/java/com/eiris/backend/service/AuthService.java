package com.eiris.backend.service;

import com.eiris.backend.dto.request.LoginRequest;
import com.eiris.backend.dto.request.RegisterRequest;
import com.eiris.backend.dto.response.AuthResponse;
import com.eiris.backend.entity.User;
import com.eiris.backend.mapper.UserMapper;
import com.eiris.backend.repository.UserRepository;
import com.eiris.backend.security.JwtUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UserMapper userMapper, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.emailService = emailService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User();
        user.setId(java.util.UUID.randomUUID());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken, userMapper.toDto(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken, userMapper.toDto(user));
    }

    public AuthResponse refreshToken(String refreshToken) {
        String userEmail = jwtUtil.extractUsername(refreshToken);
        
        if (userEmail != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String accessToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
                
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                
                return new AuthResponse(accessToken, newRefreshToken, userMapper.toDto(user));
            }
        }
        throw new IllegalArgumentException("Invalid refresh token");
    }

    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    // Generate a temporary JWT token for password reset (valid for 15 minutes usually, but we use the regular generator for simplicity here)
                    String resetToken = jwtUtil.generateToken(userDetails); 
                    emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
                    System.out.println("Email sent to: " + user.getEmail());
                },
                () -> System.out.println("MOCK EMAIL SKIPPED: Email not found " + email)
        );
    }

    public void resetPassword(String token, String newPassword) {
        String userEmail = jwtUtil.extractUsername(token);
        
        if (userEmail != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            
            if (jwtUtil.validateToken(token, userDetails)) {
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return;
            }
        }
        throw new IllegalArgumentException("Invalid or expired password reset token");
    }
}
