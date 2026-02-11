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

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        } else {
            String role = user.getRole().toUpperCase();
            if (!role.equals("ADMIN") && !role.equals("STAFF") && !role.equals("USER")) {
                throw new IllegalArgumentException("Invalid role. Must be ADMIN, STAFF, or USER");
            }
            user.setRole(role);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

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

    public User updateUser(Long id, String fullName, String email, String role,
                           String password, String phone, String documentId, String documentType) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName);
        }

        if (email != null && !email.trim().isEmpty()) {
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(email);
            user.setUsername(email);
        }

        if (role != null && !role.trim().isEmpty()) {
            String roleUpper = role.toUpperCase();
            if (!roleUpper.equals("ADMIN") && !roleUpper.equals("STAFF") && !roleUpper.equals("USER")) {
                throw new RuntimeException("Invalid role. Must be ADMIN, STAFF, or USER");
            }
            user.setRole(roleUpper);
        }

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone);
        }

        if (documentId != null && !documentId.trim().isEmpty()) {
            user.setDocumentId(documentId);
        }

        if (documentType != null && !documentType.trim().isEmpty()) {
            user.setDocumentType(documentType);
        }

        User updatedUser = userRepository.save(user);
        System.out.println("User updated successfully: " + updatedUser.getEmail());
        return updatedUser;
    }
}