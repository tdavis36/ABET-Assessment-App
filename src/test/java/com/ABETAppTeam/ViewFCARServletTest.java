package com.ABETAppTeam;

import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

class ViewFCARServletTest {

    @Test
    void testDoGet_viewAllAction() throws Exception {
        ViewFCARServlet servlet = spy(new ViewFCARServlet());
        DisplaySystemController displayControllerMock = mock(DisplaySystemController.class);
        OutcomeController outcomeControllerMock = mock(OutcomeController.class);

        doReturn(displayControllerMock).when(servlet).getDisplayController();
        doReturn(outcomeControllerMock).when(servlet).getOutcomeController();

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        RequestDispatcher dispatcherMock = mock(RequestDispatcher.class);

        when(requestMock.getParameter("action")).thenReturn("viewAll");
        when(requestMock.getRequestDispatcher("/WEB-INF/viewFCAR.jsp")).thenReturn(dispatcherMock);
        when(displayControllerMock.generateAdminDashboard()).thenReturn(new HashMap<>());
        when(outcomeControllerMock.getOutcomeDataAsJson()).thenReturn("mockOutcomeData");

        servlet.doGet(requestMock, responseMock);

        verify(displayControllerMock).generateAdminDashboard();
        verify(outcomeControllerMock).getOutcomeDataAsJson();
        verify(requestMock).setAttribute(eq("outcomeData"), eq("mockOutcomeData"));
        verify(dispatcherMock).forward(requestMock, responseMock);
    }

    @Test
    void testDoGet_fcarIdProvided() throws Exception {
        ViewFCARServlet servlet = spy(new ViewFCARServlet());
        DisplaySystemController displayControllerMock = mock(DisplaySystemController.class);
        FCARController fcarControllerMock = mock(FCARController.class);

        doReturn(displayControllerMock).when(servlet).getDisplayController();
        doReturn(fcarControllerMock).when(servlet).getFCARController();

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        HttpSession sessionMock = mock(HttpSession.class);
        RequestDispatcher dispatcherMock = mock(RequestDispatcher.class);

        FCAR fcarMock = mock(FCAR.class);
        when(requestMock.getParameter("fcarId")).thenReturn("123");
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(fcarControllerMock.getFCAR(123)).thenReturn(fcarMock);
        when(fcarMock.getAssessmentMethods()).thenReturn(new HashMap<>());
        when(fcarMock.getStudentOutcomes()).thenReturn(new HashMap<>());
        when(fcarMock.getImprovementActions()).thenReturn(new HashMap<>());
        when(displayControllerMock.getCourse(String.valueOf(anyInt()))).thenReturn(null);
        when(displayControllerMock.getUser(anyInt())).thenReturn(null);
        when(requestMock.getRequestDispatcher("/WEB-INF/viewFCAR.jsp")).thenReturn(dispatcherMock);

        servlet.doGet(requestMock, responseMock);

        verify(fcarControllerMock).getFCAR(123);
        verify(requestMock).setAttribute(eq("fcar"), eq(fcarMock));
        verify(dispatcherMock).forward(requestMock, responseMock);
    }

    @Test
    void testDoGet_professorIdProvided() throws Exception {
        ViewFCARServlet servlet = spy(new ViewFCARServlet());
        DisplaySystemController displayControllerMock = mock(DisplaySystemController.class);
        OutcomeController outcomeControllerMock = mock(OutcomeController.class);

        doReturn(displayControllerMock).when(servlet).getDisplayController();
        doReturn(outcomeControllerMock).when(servlet).getOutcomeController();

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        RequestDispatcher dispatcherMock = mock(RequestDispatcher.class);

        when(requestMock.getParameter("professorId")).thenReturn("123");
        when(displayControllerMock.generateProfessorDashboard(123)).thenReturn(new HashMap<>());
        when(requestMock.getRequestDispatcher("/WEB-INF/viewFCAR.jsp")).thenReturn(dispatcherMock);
        when(outcomeControllerMock.getOutcomeDataAsJson()).thenReturn("mockOutcomeData");

        servlet.doGet(requestMock, responseMock);

        verify(displayControllerMock).generateProfessorDashboard(123);
        verify(outcomeControllerMock).getOutcomeDataAsJson();
        verify(requestMock).setAttribute(eq("outcomeData"), eq("mockOutcomeData"));
        verify(dispatcherMock).forward(requestMock, responseMock);
    }

    @Test
    void testDoGet_courseIdProvided() throws Exception {
        ViewFCARServlet servlet = spy(new ViewFCARServlet());
        DisplaySystemController displayControllerMock = mock(DisplaySystemController.class);
        OutcomeController outcomeControllerMock = mock(OutcomeController.class);

        doReturn(displayControllerMock).when(servlet).getDisplayController();
        doReturn(outcomeControllerMock).when(servlet).getOutcomeController();

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        RequestDispatcher dispatcherMock = mock(RequestDispatcher.class);

        when(requestMock.getParameter("courseId")).thenReturn("COURSE123");
        when(displayControllerMock.generateCourseReportData("COURSE123")).thenReturn(new HashMap<>());
        when(requestMock.getRequestDispatcher("/WEB-INF/viewFCAR.jsp")).thenReturn(dispatcherMock);
        when(outcomeControllerMock.getOutcomeDataAsJson()).thenReturn("mockOutcomeData");

        servlet.doGet(requestMock, responseMock);

        verify(displayControllerMock).generateCourseReportData("COURSE123");
        verify(outcomeControllerMock).getOutcomeDataAsJson();
        verify(requestMock).setAttribute(eq("outcomeData"), eq("mockOutcomeData"));
        verify(dispatcherMock).forward(requestMock, responseMock);
    }

    @Test
    void testDoGet_noValidParameters() throws Exception {
        ViewFCARServlet servlet = spy(new ViewFCARServlet());
        DisplaySystemController displayControllerMock = mock(DisplaySystemController.class);
        OutcomeController outcomeControllerMock = mock(OutcomeController.class);

        doReturn(displayControllerMock).when(servlet).getDisplayController();
        doReturn(outcomeControllerMock).when(servlet).getOutcomeController();

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        RequestDispatcher dispatcherMock = mock(RequestDispatcher.class);

        when(requestMock.getRequestDispatcher("/WEB-INF/viewFCAR.jsp")).thenReturn(dispatcherMock);
        when(displayControllerMock.generateAdminDashboard()).thenReturn(new HashMap<>());
        when(outcomeControllerMock.getOutcomeDataAsJson()).thenReturn("mockOutcomeData");

        servlet.doGet(requestMock, responseMock);

        verify(displayControllerMock).generateAdminDashboard();
        verify(outcomeControllerMock).getOutcomeDataAsJson();
        verify(requestMock).setAttribute(eq("outcomeData"), eq("mockOutcomeData"));
        verify(dispatcherMock).forward(requestMock, responseMock);
    }
}