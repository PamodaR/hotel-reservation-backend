package com.oceanview.hotel_reservation.controller;

import com.oceanview.hotel_reservation.entity.User;
import com.oceanview.hotel_reservation.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllMembers() {
        try {
            List<User> users = authService.getAllUsers();
            System.out.println("Fetched " + users.size() + " members");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.out.println("Error fetching members: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable Long id) {
        try {
            User user = authService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try {
            authService.deleteUser(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Member deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody UpdateMemberRequest request) {
        try {
            User updatedUser = authService.updateUser(
                    id,
                    request.getFullName(),
                    request.getEmail(),
                    request.getRole(),
                    request.getPassword()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Member updated successfully");
            response.put("user", Map.of(
                    "id", updatedUser.getId(),
                    "username", updatedUser.getUsername() != null ? updatedUser.getUsername() : "",
                    "fullName", updatedUser.getFullName() != null ? updatedUser.getFullName() : "",
                    "email", updatedUser.getEmail() != null ? updatedUser.getEmail() : "",
                    "role", updatedUser.getRole()
            ));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Inner class for update request
    public static class UpdateMemberRequest {
        private String fullName;
        private String email;
        private String role;
        private String password;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}