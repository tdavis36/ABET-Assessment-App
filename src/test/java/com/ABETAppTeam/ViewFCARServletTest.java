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
class ViewFCARServletTest {
    @InjectMocks ViewFCARServlet servlet;
    @Mock HttpServletRequest req;
    @Mock HttpServletResponse res;
    @Mock HttpSession session;
    @Mock RequestDispatcher disp;

    @BeforeEach
    void init() {
        when(req.getSession()).thenReturn(session);
    }

    @Test
    void doGet_forwardViewAllWhenNoParams() throws Exception {
        when(req.getParameter("action")).thenReturn(null);
        when(req.getRequestDispatcher("/WEB-INF/viewFCAR.jsp")).thenReturn(disp);

        servlet.doGet(req, res);
        verify(disp).forward(req, res);
    }
}
