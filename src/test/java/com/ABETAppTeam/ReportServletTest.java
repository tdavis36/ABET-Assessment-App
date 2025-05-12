package com.ABETAppTeam;

import com.ABETAppTeam.controller.ReportController;
import com.ABETAppTeam.model.Admin;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReportServletTest {
    @InjectMocks ReportServlet servlet;
    @Mock HttpServletRequest req;
    @Mock HttpServletResponse res;
    @Mock HttpSession session;

    @BeforeEach
    void init() {
        when(req.getSession()).thenReturn(session);
    }

    @Test
    void doGet_forbiddenWhenNotAdmin() throws Exception {
        when(session.getAttribute("user")).thenReturn(new Object()); // not Admin
        servlet.doGet(req, res);
        verify(res).sendError(HttpServletResponse.SC_FORBIDDEN,
                "You do not have permission to view reports.");
    }

    @Test
    void doGet_defaultDashboardForAdmin() throws Exception {
        when(session.getAttribute("user")).thenReturn(Mockito.mock(com.ABETAppTeam.model.Admin.class));
        when(req.getParameter("action")).thenReturn(null);
        // stub dispatcher
        RequestDispatcher disp = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher("/WEB-INF/reportDashboard.jsp")).thenReturn(disp);

        servlet.doGet(req, res);

        verify(req).setAttribute(eq("reportTypes"), any());
        verify(req).setAttribute(eq("courses"), any());
        verify(disp).forward(req, res);
    }

    @Test
    void doGet_generateFullReport_forwardsToReportView() throws Exception {
        // arrange an admin in session
        when(session.getAttribute("user")).thenReturn(mock(Admin.class));
        when(req.getParameter("action")).thenReturn("generateFullReport");
        when(req.getParameter("reportTitle")).thenReturn("Custom Title");

        // stub controller to return a simple map
        ReportController ctrl = mock(ReportController.class);
        when(ctrl.generateFullReportData("Custom Title"))
                .thenReturn(Map.of("foo", List.of("bar")));
        // inject our stub
        try (var mocked = Mockito.mockStatic(ReportController.class)) {
            mocked.when(ReportController::getInstance).thenReturn(ctrl);

            var disp = mock(RequestDispatcher.class);
            when(req.getRequestDispatcher("/WEB-INF/reportView.jsp")).thenReturn(disp);

            servlet.doGet(req, res);

            verify(req).setAttribute("foo", List.of("bar"));
            verify(disp).forward(req, res);
        }
    }

}
