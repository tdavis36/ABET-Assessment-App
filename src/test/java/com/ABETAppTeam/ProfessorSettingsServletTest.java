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
class ProfessorSettingsServletTest {
    @InjectMocks ProfessorSettingsServlet servlet;
    @Mock HttpServletRequest req;
    @Mock HttpServletResponse res;
    @Mock HttpSession session;
    @Mock RequestDispatcher disp;

    @BeforeEach
    void init() {
        when(req.getSession()).thenReturn(session);
    }

    @Test
    void doGet_redirectWhenNotLoggedIn() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);
        servlet.doGet(req, res);
        verify(res).sendRedirect("/index");
    }

    @Test
    void doGet_forbiddenWhenNotProfessor() throws Exception {
        when(session.getAttribute("user")).thenReturn(new Object());
        servlet.doGet(req, res);
        verify(res).sendError(HttpServletResponse.SC_FORBIDDEN,
                "Access denied. Professor privileges required.");
    }

    @Test
    void doGet_showSettingsWhenProfessor() throws Exception {
        com.ABETAppTeam.model.Professor prof = new com.ABETAppTeam.model.Professor();
        when(session.getAttribute("user")).thenReturn(prof);
        when(req.getRequestDispatcher("/WEB-INF/professorSettings.jsp")).thenReturn(disp);

        servlet.doGet(req, res);
        verify(req).setAttribute("activePage", "settings");
        verify(disp).forward(req, res);
    }
}
