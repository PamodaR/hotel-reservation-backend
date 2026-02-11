package com.oceanview.hotel_reservation.controller;

import com.oceanview.hotel_reservation.entity.User;
import com.oceanview.hotel_reservation.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
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

    // Get all users (Admin only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = authService.getAllUsers();

            List<Map<String, Object>> userList = users.stream()
                    .map(this::buildUserMap)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", userList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch users: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = authService.getUserById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", buildUserMap(user));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Update user (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            User updatedUser = authService.updateUser(
                    id,
                    request.getFullName(),
                    request.getEmail(),
                    request.getRole(),
                    request.getPassword(),
                    request.getPhone(),
                    request.getDocumentId(),
                    request.getDocumentType()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User updated successfully");
            response.put("user", buildUserMap(updatedUser));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Delete user (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            authService.deleteUser(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User deleted successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Inner class for update request
    public static class UpdateUserRequest {
        private String fullName;
        private String email;
        private String role;
        private String password;
        private String phone;
        private String documentId;
        private String documentType;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
    }
}