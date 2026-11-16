package com.college.internship.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class SessionAuthorizationInterceptorTest {
    private final SessionAuthorizationInterceptor interceptor = new SessionAuthorizationInterceptor();

    @Test
    void loginEndpointIsPublic() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void protectedApiRejectsMissingSession() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/students/7");
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(401, response.getStatus());
    }

    @Test
    void studentCannotReadAnotherStudentsProfile() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/students/8");
        request.getSession(true).setAttribute("authenticatedUserId", 7);
        request.getSession().setAttribute("authenticatedUserRole", "STUDENT");
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
    }

    @Test
    void studentCanReadOwnProfile() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/students/7");
        request.getSession(true).setAttribute("authenticatedUserId", 7);
        request.getSession().setAttribute("authenticatedUserRole", "STUDENT");
        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void studentCannotReviewApplications() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/applications/12/status");
        request.getSession(true).setAttribute("authenticatedUserId", 7);
        request.getSession().setAttribute("authenticatedUserRole", "STUDENT");
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
    }
}
