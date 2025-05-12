// Fixed ProfessorServletTest.java
package com.ABETAppTeam;

import com.ABETAppTeam.model.Professor;
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
class ProfessorServletTest {
    @InjectMocks ProfessorServlet servlet;
    @Mock HttpServletRequest req;
    @Mock HttpServletResponse res;
    @Mock HttpSession session;
    @Mock RequestDispatcher disp;

    @BeforeEach
    void init() {
        // Set up session mocking
        when(req.getSession()).thenReturn(session);
        when(req.getSession(false)).thenReturn(session);

        // Set up context path for URL resolution
        when(req.getContextPath()).thenReturn("/ABETApp");
        when(req.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/ABETApp"));

        // Set up request dispatcher for all JSPs
        when(req.getRequestDispatcher(anyString())).thenReturn(disp);
    }

    @Test
    void doGet_redirectToLoginWhenSessionNull() throws Exception {
        // Override the default session mock to return null
        when(req.getSession(false)).thenReturn(null);

        servlet.doGet(req, res);

        // Verify redirect with absolute path
        verify(res).sendRedirect("/index");
    }

    @Test
    void doGet_forbiddenWhenNotProfessor() throws Exception {
        // Set up a non-Professor user
        when(session.getAttribute("user")).thenReturn(new com.ABETAppTeam.model.Admin());

        servlet.doGet(req, res);

        // Verify sending error
        verify(res).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void doGet_forProfessor_setsCacheHeadersAndForwards() throws Exception {
        // Set up a Professor user
        Professor prof = new Professor();
        when(session.getAttribute("user")).thenReturn(prof);

        // Set up needed request dispatcher
        when(req.getRequestDispatcher("/WEB-INF/professor.jsp")).thenReturn(disp);

        servlet.doGet(req, res);

        // Verify cache headers
        verify(res).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        verify(res).setHeader("Pragma", "no-cache");
        verify(res).setDateHeader(eq("Expires"), anyLong());

        // Verify forward
        verify(disp).forward(req, res);
    }
}