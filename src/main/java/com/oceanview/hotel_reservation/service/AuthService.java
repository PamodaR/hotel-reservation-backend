package com.oceanview.hotel_reservation.service;

import com.oceanview.hotel_reservation.entity.User;
import com.oceanview.hotel_reservation.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    public User register(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (user.getUsername() != null && userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Validate and set role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        } else {
            // Validate that role is either ADMIN, STAFF, or USER
            String role = user.getRole().toUpperCase();
            if (!role.equals("ADMIN") && !role.equals("STAFF") && !role.equals("USER")) {
                throw new IllegalArgumentException("Invalid role. Must be ADMIN, STAFF, or USER");
            }
            user.setRole(role);
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // NEW METHODS FOR MEMBER MANAGEMENT

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        System.out.println("User deleted successfully with id: " + id);
    }

    public User updateUser(Long id, String fullName, String email, String role, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update full name if provided
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName);
        }

        // Update email if provided and not already taken by another user
        if (email != null && !email.trim().isEmpty()) {
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(email);
            user.setUsername(email); // Keep username in sync with email
        }

        // Update role if provided
        if (role != null && !role.trim().isEmpty()) {
            String roleUpper = role.toUpperCase();
            if (!roleUpper.equals("ADMIN") && !roleUpper.equals("STAFF") && !roleUpper.equals("USER")) {
                throw new RuntimeException("Invalid role. Must be ADMIN, STAFF, or USER");
            }
            user.setRole(roleUpper);
        }

        // Update password if provided
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        User updatedUser = userRepository.save(user);
        System.out.println("User updated successfully: " + updatedUser.getEmail());
        return updatedUser;
    }
}