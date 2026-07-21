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

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
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
        // In a real application, you would generate a secure reset token, save it to the DB,
        // and send an email using an SMTP service like JavaMailSender.
        // For now, we mock it and just log to the console.
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> System.out.println("MOCK EMAIL SENT: Password reset link sent to " + user.getEmail()),
                () -> System.out.println("MOCK EMAIL SKIPPED: Email not found " + email)
        );
        // We do not throw an error if the email is not found to prevent email enumeration attacks.
    }
}
