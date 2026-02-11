package com.oceanview.hotel_reservation.controller;

import com.oceanview.hotel_reservation.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.oceanview.hotel_reservation.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login attempt for email: " + loginRequest.getEmail());

            User user = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", buildUserMap(user));

            System.out.println("Login successful for: " + user.getEmail() + " with role: " + user.getRole());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.out.println("Login failed: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            System.out.println("Registration attempt for: " + registerRequest.getEmail());

            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Passwords do not match");
                return ResponseEntity.badRequest().body(response);
            }

            User user = new User();
            user.setUsername(registerRequest.getEmail());
            user.setFullName(registerRequest.getName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setRole(registerRequest.getRole());
            user.setPhone(registerRequest.getPhone());
            user.setDocumentId(registerRequest.getDocumentId());
            user.setDocumentType(registerRequest.getDocumentType());

            User registered = authService.register(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("user", buildUserMap(registered));

            System.out.println("Registration successful for: " + registered.getEmail() + " with role: " + registered.getRole());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifySession(@RequestParam Long userId) {
        try {
            User user = authService.getUserById(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", buildUserMap(user));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid session");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Shared helper to build user response map
    private Map<String, Object> buildUserMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername() != null ? user.getUsername() : "");
        userMap.put("fullName", user.getFullName() != null ? user.getFullName() : "");
        userMap.put("email", user.getEmail() != null ? user.getEmail() : "");
        userMap.put("role", user.getRole());
        userMap.put("phone", user.getPhone() != null ? user.getPhone() : "");
        userMap.put("documentId", user.getDocumentId() != null ? user.getDocumentId() : "");
        userMap.put("documentType", user.getDocumentType() != null ? user.getDocumentType() : "");
        return userMap;
    }

    // Inner class for login request
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // Inner class for register request
    public static class RegisterRequest {
        private String name;
        private String email;
        private String role;
        private String password;
        private String confirmPassword;
        private String phone;
        private String documentId;
        private String documentType;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
    }
}