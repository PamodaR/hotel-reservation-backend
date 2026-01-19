package com.oceanview.hotel_reservation.config;

import com.oceanview.hotel_reservation.entity.User;
import com.oceanview.hotel_reservation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            // Create default admin user if no users exist
            if (userRepository.count() == 0) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Hash the password
                admin.setFullName("Administrator");
                admin.setEmail("admin@oceanview.com");
                admin.setRole("ADMIN");
                userRepository.save(admin);

                User staff = new User();
                staff.setUsername("staff");
                staff.setPassword(passwordEncoder.encode("staff123")); // Hash the password
                staff.setFullName("Staff Member");
                staff.setEmail("staff@oceanview.com");
                staff.setRole("STAFF");
                userRepository.save(staff);

                System.out.println("Default users created:");
                System.out.println("Admin - username: admin, password: admin123");
                System.out.println("Staff - username: staff, password: staff123");
            }
        };
    }
}