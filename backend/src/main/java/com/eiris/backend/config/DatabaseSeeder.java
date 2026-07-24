package com.eiris.backend.config;

import com.eiris.backend.entity.User;
import com.eiris.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Running Database Seeder...");

        // Ensure Admin user exists
        String adminEmail = "admin@eiris.in";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setId(UUID.randomUUID());
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            logger.info("Created default Admin user: {}", adminEmail);
        } else {
            logger.info("Admin user already exists.");
            // Update password just in case it was inserted incorrectly
            User admin = userRepository.findByEmail(adminEmail).get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(admin);
            logger.info("Updated Admin password to match BCrypt formatting.");
        }

        // Ensure Agency user exists
        String agencyEmail = "agency@eiris.in";
        if (userRepository.findByEmail(agencyEmail).isEmpty()) {
            User agency = new User();
            agency.setId(UUID.randomUUID());
            agency.setEmail(agencyEmail);
            agency.setPassword(passwordEncoder.encode("agency123"));
            agency.setRole("AGENCY");
            userRepository.save(agency);
            logger.info("Created default Agency user: {}", agencyEmail);
        } else {
            logger.info("Agency user already exists.");
            User agency = userRepository.findByEmail(agencyEmail).get();
            agency.setPassword(passwordEncoder.encode("agency123"));
            userRepository.save(agency);
            logger.info("Updated Agency password to match BCrypt formatting.");
        }
        
        logger.info("Database Seeder completed.");
    }
}
