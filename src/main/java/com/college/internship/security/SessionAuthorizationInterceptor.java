package com.college.internship.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API authorization based only on the identity and role saved in the server session.
 * Explicitly allows the routes used by the application and denies unknown routes.
 */
public class SessionAuthorizationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Public endpoints.
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) return true;
        if (path.equals("/api/auth/me") || path.equals("/api/auth/logout")) return true;
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

        // Administrators may use protected application-management APIs.
        if (admin) return true;

        // Students may only view/update their own profile.
        if (path.matches("/api/students/\\d+")) {
            int requestedId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            return requestedId == userId || forbidden(response);
        }

        // Students may only add/remove skills on their own profile.
        if (path.matches("/api/students/\\d+/skills(?:/\\d+)?")) {
            String[] parts = path.split("/");
            int requestedId = Integer.parseInt(parts[3]);
            return requestedId == userId || forbidden(response);
        }

        // Students may submit applications and view only their own applications.
        if (path.equals("/api/applications") && HttpMethod.POST.matches(method)) return true;
        if (path.matches("/api/applications/student/\\d+")) {
            int requestedId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            return requestedId == userId || forbidden(response);
        }

        return forbidden(response);
    }

    private boolean unauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\\"error\\":\\"Authentication required.\\"}");
        return false;
    }

    private boolean forbidden(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\\"error\\":\\"You are not authorized to access this resource.\\"}");
        return false;
    }
}
