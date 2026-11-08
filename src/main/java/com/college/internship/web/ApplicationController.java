package com.college.internship.web;

import com.college.internship.exception.DatabaseException;
import com.college.internship.exception.ValidationException;
import com.college.internship.model.Application;
import com.college.internship.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Internship Application endpoints.
 */
@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService = new ApplicationService();

    /**
     * GET /api/applications — all applications (admin)
     */
    @GetMapping
    public ResponseEntity<?> getAllApplications() {
        try {
            List<Application> applications = applicationService.getAllApplications();
            return ResponseEntity.ok(applications);
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/applications/student/{studentId} — applications for a student
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentApplications(@PathVariable int studentId) {
        try {
            List<Application> applications = applicationService.getStudentApplications(studentId);
            return ResponseEntity.ok(applications);
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/applications — apply for internship
     * Body: { "studentId": 1, "internshipId": 2, "coverLetter": "..." }
     */
    @PostMapping
    public ResponseEntity<?> applyForInternship(@RequestBody Map<String, Object> body) {
        try {
            Object sidObj = body.get("studentId");
            int studentId = (sidObj instanceof Number) ? ((Number) sidObj).intValue()
                    : Integer.parseInt(sidObj.toString());
            Object iidObj = body.get("internshipId");
            int internshipId = (iidObj instanceof Number) ? ((Number) iidObj).intValue()
                    : Integer.parseInt(iidObj.toString());
            String coverLetter = (String) body.get("coverLetter");

            applicationService.applyForInternship(studentId, internshipId, coverLetter);
            return ResponseEntity.ok(Map.of("message", "Application submitted successfully."));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/applications/{id}/status — update application status
     * Body: { "status": "ACCEPTED", "feedback": "..." }
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable int id,
                                           @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            if (statusStr == null || statusStr.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required."));
            }
            Application.Status status = Application.Status.valueOf(statusStr.toUpperCase());
            String feedback = body.getOrDefault("feedback", "");
            applicationService.updateStatus(id, status, feedback);
            return ResponseEntity.ok(Map.of("message", "Application status updated."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status value."));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
