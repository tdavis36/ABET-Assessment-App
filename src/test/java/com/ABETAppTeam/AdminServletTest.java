// Fixed AdminServletTest.java
package com.ABETAppTeam;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminServletTest {
    @InjectMocks AdminServlet servlet;
    @Mock HttpServletRequest req;
    @Mock HttpServletResponse res;
    @Mock HttpSession session;
    @Mock RequestDispatcher disp;

    @BeforeEach
    void init() {
        when(req.getSession()).thenReturn(session);
        when(req.getSession(false)).thenReturn(session); // Add this line

        // Mock any request dispatcher call
        when(req.getRequestDispatcher(anyString())).thenReturn(disp);

        // Set up URL for context path resolution
        when(req.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/ABETApp"));
        when(req.getContextPath()).thenReturn("/ABETApp");
    }

    @Test
    void doGet_forbiddenWhenNotAdmin() throws Exception {
        // Use Professor instead of generic Object to avoid ClassCastException
        when(session.getAttribute("user")).thenReturn(new com.ABETAppTeam.model.Professor());
        servlet.doGet(req, res);
        verify(res).sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
    }

    @Test
    void doGet_forwardDashboardWhenAdmin() throws Exception {
        com.ABETAppTeam.model.Admin admin = new com.ABETAppTeam.model.Admin();
        when(session.getAttribute("user")).thenReturn(admin);
        when(req.getRequestDispatcher("/WEB-INF/admin.jsp")).thenReturn(disp);
        servlet.doGet(req, res);
        verify(disp).forward(req, res);
    }
}