package com.college.internship.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionAuthorizationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) return true;
        if (path.equals("/api/internships/open") && HttpMethod.GET.matches(method)) return true;
        if (path.equals("/api/internships") && HttpMethod.GET.matches(method)) return true;
        if (path.equals("/api/skills") && HttpMethod.GET.matches(method)) return true;

        HttpSession session = request.getSession(false);
        Object idValue = session == null ? null : session.getAttribute("authenticatedUserId");
        Object roleValue = session == null ? null : session.getAttribute("authenticatedUserRole");
        if (!(idValue instanceof Integer) || !(roleValue instanceof String)) return unauthorized(response);

        int userId = (Integer) idValue;
        String role = (String) roleValue;
        boolean admin = "ADMIN".equals(role);
        request.setAttribute("authenticatedUserId", userId);
        request.setAttribute("authenticatedUserRole", role);

        if (path.equals("/api/students") && HttpMethod.GET.matches(method) && !admin) return forbidden(response);
        if (path.matches("/api/students/\\d+")) {
            int requestedId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            if (!admin && requestedId != userId) return forbidden(response);
        }
        if (path.matches("/api/students/\\d+/skills(?:/\\d+)?")) {
            String[] parts = path.split("/");
            if (!admin && Integer.parseInt(parts[3]) != userId) return forbidden(response);
        }
        if (path.equals("/api/applications") && HttpMethod.GET.matches(method) && !admin) return forbidden(response);
        if (path.matches("/api/applications/student/\\d+")) {
            int requestedId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            if (!admin && requestedId != userId) return forbidden(response);
        }
        if (path.matches("/api/applications/\\d+/status") && !admin) return forbidden(response);
        if (path.equals("/api/internships") && !HttpMethod.GET.matches(method) && !admin) return forbidden(response);
        if (path.matches("/api/internships/\\d+") && !admin) return forbidden(response);
        return true;
    }
    private boolean unauthorized(HttpServletResponse r) throws Exception {
        r.setStatus(401); r.setContentType("application/json"); r.getWriter().write("{\"error\":\"Authentication required.\"}"); return false;
    }
    private boolean forbidden(HttpServletResponse r) throws Exception {
        r.setStatus(403); r.setContentType("application/json"); r.getWriter().write("{\"error\":\"You are not authorized to access this resource.\"}"); return false;
    }
}