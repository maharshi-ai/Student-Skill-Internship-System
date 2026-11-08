package com.college.internship.web;

import com.college.internship.exception.AuthenticationException;
import com.college.internship.exception.DatabaseException;
import com.college.internship.exception.ValidationException;
import com.college.internship.model.Student;
import com.college.internship.model.User;
import com.college.internship.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication endpoints (login, register).
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService = new AuthService();

    /**
     * POST /api/auth/login
     * Accepts: { "email": "...", "password": "..." }
     * Returns: { "id": 1, "name": "...", "role": "STUDENT" } or 401
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        try {
            User user = authService.login(email, password);
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("role", user.getRole().name());
            response.put("email", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error: " + e.getMessage()));
        }
    }

    /**
     * POST /api/auth/register
     * Accepts: { name, email, password, department, phone, cgpa, graduationYear, bio }
     * Returns: 200 with user info or 400 with error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            String department = (String) body.get("department");
            String phone = (String) body.getOrDefault("phone", "");
            double cgpa = 0.0;
            Object cgpaObj = body.get("cgpa");
            if (cgpaObj instanceof Number) {
                cgpa = ((Number) cgpaObj).doubleValue();
            } else if (cgpaObj instanceof String) {
                cgpa = Double.parseDouble((String) cgpaObj);
            }
            int graduationYear = 2025;
            Object gyObj = body.get("graduationYear");
            if (gyObj instanceof Number) {
                graduationYear = ((Number) gyObj).intValue();
            } else if (gyObj instanceof String) {
                graduationYear = Integer.parseInt((String) gyObj);
            }
            String bio = (String) body.getOrDefault("bio", "");

            Student student = authService.registerStudent(name, email, password, department,
                    phone, cgpa, graduationYear, bio);

            Map<String, Object> response = new HashMap<>();
            response.put("id", student.getId());
            response.put("name", student.getName());
            response.put("role", student.getRole().name());
            response.put("message", "Registration successful!");
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
