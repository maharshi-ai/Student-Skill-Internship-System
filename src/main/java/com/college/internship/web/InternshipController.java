package com.college.internship.web;

import com.college.internship.exception.DatabaseException;
import com.college.internship.exception.ValidationException;
import com.college.internship.model.Internship;
import com.college.internship.service.InternshipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Internship listing endpoints.
 */
@RestController
@RequestMapping("/api/internships")
@CrossOrigin(origins = "*")
public class InternshipController {

    private final InternshipService internshipService = new InternshipService();

    /**
     * GET /api/internships — all internships
     */
    @GetMapping
    public ResponseEntity<?> getAllInternships() {
        try {
            List<Internship> internships = internshipService.getAllInternships();
            return ResponseEntity.ok(internships);
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/internships/open — open internships only
     */
    @GetMapping("/open")
    public ResponseEntity<?> getOpenInternships() {
        try {
            List<Internship> internships = internshipService.getOpenInternships();
            return ResponseEntity.ok(internships);
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/internships — create a new internship (admin only)
     * Body: { title, company, location, description, requirements, stipend, deadline, role }
     */
    @PostMapping
    public ResponseEntity<?> createInternship(@RequestBody Map<String, Object> body) {
        String role = (String) body.getOrDefault("role", "");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Admin access required."));
        }
        try {
            String title = (String) body.get("title");
            String company = (String) body.get("company");
            String location = (String) body.get("location");
            String description = (String) body.get("description");
            String requirements = (String) body.get("requirements");
            String stipend = (String) body.getOrDefault("stipend", "Unpaid");
            String deadlineStr = (String) body.get("deadline");
            LocalDate deadline = (deadlineStr != null) ? LocalDate.parse(deadlineStr) : null;

            int id = internshipService.createInternship(title, company, location,
                    description, requirements, stipend, deadline);
            return ResponseEntity.ok(Map.of("id", id, "message", "Internship created successfully."));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/internships/{id} — update internship
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInternship(@PathVariable int id,
                                               @RequestBody Map<String, Object> body) {
        String role = (String) body.getOrDefault("role", "");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Admin access required."));
        }
        try {
            Internship internship = new Internship();
            internship.setId(id);
            internship.setTitle((String) body.get("title"));
            internship.setCompany((String) body.get("company"));
            internship.setLocation((String) body.get("location"));
            internship.setDescription((String) body.get("description"));
            internship.setRequirements((String) body.get("requirements"));
            internship.setStipend((String) body.getOrDefault("stipend", "Unpaid"));
            String deadlineStr = (String) body.get("deadline");
            if (deadlineStr != null) {
                internship.setDeadline(LocalDate.parse(deadlineStr));
            }
            String statusStr = (String) body.getOrDefault("status", "OPEN");
            internship.setStatus(Internship.Status.valueOf(statusStr.toUpperCase()));

            internshipService.updateInternship(internship);
            return ResponseEntity.ok(Map.of("message", "Internship updated successfully."));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/internships/{id} — delete internship (admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInternship(@PathVariable int id,
                                               @RequestParam(required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Admin access required."));
        }
        try {
            internshipService.deleteInternship(id);
            return ResponseEntity.ok(Map.of("message", "Internship deleted successfully."));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
