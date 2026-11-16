package com.college.internship.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Controller that serves HTML pages and handles page-level routing.
 * HTML files live in src/main/resources/static/ and are served directly.
 */
@Controller
public class PageController {

    @GetMapping("/")
    public void root(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login");
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() throws IOException {
        return serveHtml("static/login.html");
    }

    @GetMapping("/register")
    public ResponseEntity<String> register() throws IOException {
        return serveHtml("static/register.html");
    }

    @GetMapping("/admin/dashboard")
    public ResponseEntity<String> adminDashboard() throws IOException {
        return serveHtml("static/admin-dashboard.html");
    }

    @GetMapping("/student/dashboard")
    public ResponseEntity<String> studentDashboard() throws IOException {
        return serveHtml("static/student-dashboard.html");
    }

    private ResponseEntity<String> serveHtml(String classpathPath) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpathPath);
        try (InputStream is = resource.getInputStream()) {
            String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(new MediaType("text", "html", StandardCharsets.UTF_8))
                    .body(html);
        }
    }
}
