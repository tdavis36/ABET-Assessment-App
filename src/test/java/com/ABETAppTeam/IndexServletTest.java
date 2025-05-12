// Fixed IndexServletTest.java
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
class IndexServletTest {
    @InjectMocks IndexServlet servlet;
    @Mock HttpServletRequest req;
    @Mock HttpServletResponse res;
    @Mock HttpSession session;
    @Mock RequestDispatcher disp;

    @BeforeEach
    void init() {
        // Set up session mocking
        when(req.getSession()).thenReturn(session);

        // Set up context path for URL resolution
        when(req.getContextPath()).thenReturn("");  // Root context path

        // Set up request dispatcher for JSPs
        when(req.getRequestDispatcher(anyString())).thenReturn(disp);
    }

    @Test
    void doGet_showLoginWhenNoSession() throws Exception {
        // Override the session to be null for this specific test
        when(req.getSession(false)).thenReturn(null);
        when(req.getRequestDispatcher("/index.jsp")).thenReturn(disp);

        servlet.doGet(req, res);

        verify(disp).forward(req, res);
    }

    @Test
    void doGet_redirectAdminWhenLoggedIn() throws Exception {
        com.ABETAppTeam.model.Admin admin = new com.ABETAppTeam.model.Admin();
        when(session.getAttribute("user")).thenReturn(admin);

        servlet.doGet(req, res);

        verify(res).sendRedirect("/admin");
    }

    @Test
    void doGet_redirectProfessorWhenLoggedIn() throws Exception {
        com.ABETAppTeam.model.Professor prof = new com.ABETAppTeam.model.Professor();
        when(session.getAttribute("user")).thenReturn(prof);

        servlet.doGet(req, res);

        verify(res).sendRedirect("/professor");
    }
}