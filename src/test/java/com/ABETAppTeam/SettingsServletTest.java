package com.ABETAppTeam;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsServletTest {
    @InjectMocks SettingsServlet servlet;
    @Mock HttpServletRequest req;
    @Mock HttpServletResponse res;
    @Mock HttpSession session;
    @Mock RequestDispatcher disp;

    @BeforeEach
    void init() {
        when(req.getSession()).thenReturn(session);
        when(req.getSession(false)).thenReturn(session);
    }

    @Test
    void doGet_redirectWhenNotLoggedIn() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);
        servlet.doGet(req, res);
        verify(res).sendRedirect("/index");
    }

    @Test
    void doGet_forbiddenWhenNotAdmin() throws Exception {
        when(session.getAttribute("user")).thenReturn(new Object());
        servlet.doGet(req, res);
        verify(res).sendError(HttpServletResponse.SC_FORBIDDEN,
                "Access denied. Admin privileges required.");
    }

    @Test
    void doGet_forwardWhenAdmin() throws Exception {
        com.ABETAppTeam.model.Admin admin = new com.ABETAppTeam.model.Admin();
        when(session.getAttribute("user")).thenReturn(admin);
        when(req.getRequestDispatcher("/WEB-INF/settings.jsp")).thenReturn(disp);
        servlet.doGet(req, res);
        verify(req).setAttribute("activePage", "settings");
        verify(disp).forward(req, res);
    }
}
