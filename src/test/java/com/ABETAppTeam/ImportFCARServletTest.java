package com.ABETAppTeam;

import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.FCAR;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImportFCARServletTest {
    @InjectMocks ImportFCARServlet servlet;
    @Mock HttpServletRequest req;
    @Mock HttpServletResponse res;
    @Mock HttpSession session;

    @BeforeEach
    void init() {
        when(req.getSession()).thenReturn(session);
    }

    @Test
    void doPost_redirectWhenNotLoggedIn() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);
        servlet.doPost(req, res);
        verify(res).sendRedirect("/index");
    }

    @Test
    void doPost_forbiddenWhenNotAdmin() throws Exception {
        when(session.getAttribute("user")).thenReturn(new Object());
        servlet.doPost(req, res);
        verify(res).sendError(HttpServletResponse.SC_FORBIDDEN,
                "Access denied. Admin privileges required.");
    }

    @Test
    void doPost_successfulImport_setsSuccessAndRedirects() throws Exception {
        // login as admin
        Admin admin = new Admin();
        when(session.getAttribute("user")).thenReturn(admin);
        // mock a nonempty CSV part
        Part p = mock(Part.class);
        when(p.getSize()).thenReturn(100L);
        when(servlet.getFileName(p)).thenReturn("data.csv");
        when(req.getPart("fcarFile")).thenReturn(p);
        when(req.getParameter("headerRow")).thenReturn("on");

        // spy the servlet to stub processCSVFile
        ImportFCARServlet spy = Mockito.spy(servlet);
        List<FCAR> list = List.of(new FCAR(1,"CS101",1,"F",2025));
        doReturn(list).when(spy).processCSVFile(eq(p), eq(true), eq(admin));

        spy.doPost(req, res);

        verify(session).setAttribute("successMessage", "Successfully imported 1 FCARs");
        verify(res).sendRedirect("/settings");
    }
}
