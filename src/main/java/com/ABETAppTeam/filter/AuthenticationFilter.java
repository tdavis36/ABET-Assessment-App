package com.ABETAppTeam.filter;

import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter to protect routes that require authentication
 */
@WebFilter(urlPatterns = {"/AdminServlet/*", "/ProfessorServlet/*", "/ViewFCARServlet/*", "/ReportsServlet/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the current session (don't create a new one)
        HttpSession session = httpRequest.getSession(false);

        // Check if user is logged in
        boolean isLoggedIn = session != null && session.getAttribute("user") != null;

        if (isLoggedIn) {
            User user = (User) session.getAttribute("user");
            String requestURI = httpRequest.getRequestURI();

            // Check if user has permission to access the requested resource
            if (requestURI.contains("/AdminServlet") && !(user instanceof Admin)) {
                // User is not an admin but trying to access admin resources
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/index");
                return;
            }

            if (requestURI.contains("/ReportServlet")) {
                // For report generation, we should allow both admins and professors,
                // but possibly with different levels of access
                String action = httpRequest.getParameter("action");

                // Actions that only admins can perform
                boolean adminOnlyAction = action != null &&
                        (action.equals("deleteReport") ||
                                action.equals("modifyReportSettings"));

                if (adminOnlyAction && !(user instanceof Admin)) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/index");
                    return;
                }
            }

            // Allow the request to proceed
            chain.doFilter(request, response);
        } else {
            // User is not logged in, redirect to login page
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index");
        }
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}