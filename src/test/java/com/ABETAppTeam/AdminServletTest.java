package com.ABETAppTeam;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class AdminServletTest {

    /**
     * Test class for the AdminServlet class, focusing on the doGet method.
     * The doGet method is responsible for forwarding HTTP GET requests to the
     * "/WEB-INF/admin.jsp" view, and this test ensures the forwarding functionality works correctly.
     */

    @Test
    void testDoGet_ForwardsToAdminJsp() throws ServletException, IOException {
        // Arrange
        AdminServlet adminServlet = new AdminServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/WEB-INF/admin.jsp")).thenReturn(dispatcher);

        // Act
        adminServlet.doGet(request, response);

        // Assert
        verify(request).getRequestDispatcher("/WEB-INF/admin.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_HandlesNullRequestDispatcher() {
        // Arrange
        AdminServlet adminServlet = new AdminServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestDispatcher("/WEB-INF/admin.jsp")).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> adminServlet.doGet(request, response));
    }
}