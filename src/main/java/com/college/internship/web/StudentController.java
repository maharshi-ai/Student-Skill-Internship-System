package com.college.internship.web;

import com.college.internship.exception.DatabaseException;
import com.college.internship.exception.ValidationException;
import com.college.internship.model.Skill;
import com.college.internship.model.Student;
import com.college.internship.model.StudentSkill;
import com.college.internship.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Student profile and skill management endpoints.
 */
@RestController
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService = new StudentService();

    /**
     * GET /api/students — all students with skills
     */
    @GetMapping("/api/students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/students/{id} — single student profile with skills
     */
    @GetMapping("/api/students/{id}")
    public ResponseEntity<?> getStudent(@PathVariable int id) {
        try {
            Student student = studentService.getStudentProfile(id);
            return ResponseEntity.ok(student);
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/students/{id} — update student profile
     */
    @PutMapping("/api/students/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable int id,
                                            @RequestBody Map<String, Object> body) {
        try {
            Student student = new Student();
            student.setId(id);
            student.setName((String) body.get("name"));
            student.setDepartment((String) body.getOrDefault("department", ""));
            student.setPhone((String) body.getOrDefault("phone", ""));
            Object cgpaObj = body.get("cgpa");
            if (cgpaObj instanceof Number) student.setCgpa(((Number) cgpaObj).doubleValue());
            else if (cgpaObj instanceof String) student.setCgpa(Double.parseDouble((String) cgpaObj));
            Object gyObj = body.get("graduationYear");
            if (gyObj instanceof Number) student.setGraduationYear(((Number) gyObj).intValue());
            else if (gyObj instanceof String) student.setGraduationYear(Integer.parseInt((String) gyObj));
            student.setBio((String) body.getOrDefault("bio", ""));

            studentService.updateProfile(student);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully."));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/skills — all master skills
     */
    @GetMapping("/api/skills")
    public ResponseEntity<?> getAllSkills() {
        try {
            List<Skill> skills = studentService.getAllMasterSkills();
            return ResponseEntity.ok(skills);
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/students/{id}/skills — add skill to student
     * Body: { "skillId": 1, "level": "BEGINNER" }
     */
    @PostMapping("/api/students/{id}/skills")
    public ResponseEntity<?> addSkill(@PathVariable int id,
                                       @RequestBody Map<String, Object> body) {
        try {
            Object skillIdObj = body.get("skillId");
            int skillId = (skillIdObj instanceof Number) ? ((Number) skillIdObj).intValue()
                    : Integer.parseInt(skillIdObj.toString());
            String levelStr = (String) body.getOrDefault("level", "BEGINNER");
            StudentSkill.ProficiencyLevel level = StudentSkill.ProficiencyLevel.valueOf(levelStr.toUpperCase());
            studentService.addSkillToStudent(id, skillId, level);
            return ResponseEntity.ok(Map.of("message", "Skill added successfully."));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid proficiency level."));
        }
    }

    /**
     * DELETE /api/students/{id}/skills/{skillEntryId} — remove skill from student
     */
    @DeleteMapping("/api/students/{id}/skills/{skillEntryId}")
    public ResponseEntity<?> removeSkill(@PathVariable int id,
                                          @PathVariable int skillEntryId) {
        try {
            studentService.removeSkillFromStudent(skillEntryId);
            return ResponseEntity.ok(Map.of("message", "Skill removed successfully."));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
