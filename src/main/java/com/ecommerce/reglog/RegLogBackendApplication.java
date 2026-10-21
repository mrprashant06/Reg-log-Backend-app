package com.ecommerce.reglog;

import com.ecommerce.reglog.entity.User;
import com.ecommerce.reglog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class RegLogBackendApplication {

    private static final Logger logger = LoggerFactory.getLogger(RegLogBackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RegLogBackendApplication.class, args);
        logger.info("""
                ====================================================================
                E-Commerce Authentication & Database Management Backend Initialized!
                API Documentation (Swagger UI): http://localhost:8080/swagger-ui.html
                OpenAPI JSON Spec:             http://localhost:8080/v3/api-docs
                ====================================================================
                """);
    }

    /**
     * Bootstraps default administrator account if not present in the database.
     */
    @Bean
    public CommandLineRunner bootstrapData(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder) {
        return args -> {
            final String defaultAdminEmail = "admin@ecommerce.com";
            if (!userRepository.existsByEmailIgnoreCase(defaultAdminEmail)) {
                User adminUser = new User();
                adminUser.setFullName("Store Administrator");
                adminUser.setEmail(defaultAdminEmail);
                adminUser.setPasswordHash(passwordEncoder.encode("AdminPassword123!"));
                adminUser.setPhone("+1234567890");
                adminUser.setAuthPreference("PASSWORD");
                adminUser.setIsEmailVerified(true);
                adminUser.setIsPhoneVerified(true);
                adminUser.setRole("ROLE_ADMIN");
                adminUser.setStatus("ACTIVE");

                userRepository.save(adminUser);
                logger.info("Default Admin account seeded: {} / AdminPassword123!", defaultAdminEmail);
            }
        };
    }
}
