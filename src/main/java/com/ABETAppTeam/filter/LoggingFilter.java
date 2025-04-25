package com.ABETAppTeam.filter;

import com.ABETAppTeam.User;
import com.ABETAppTeam.service.LoggingService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter to set up logging context for each request.
 * This filter adds request-specific information to the logging context,
 * such as user ID, session ID, and request URL.
 */
@WebFilter("/*")
public class LoggingFilter implements Filter {

    private final LoggingService logger = LoggingService.getInstance();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {

            // Generate a unique request ID for tracking this request through the logs
            String requestId = generateRequestId();

            try {
                // Set up the logging context with request info
                setupLoggingContext(httpRequest, requestId);

                // Process the request
                String timerId = logger.startTimer("httpRequest");
                chain.doFilter(request, response);

                // Log the request completion
                logRequestCompletion(httpRequest, httpResponse, timerId);
            } finally {
                // Always clean up the logging context
                cleanupLoggingContext();
            }
        } else {
            // If not an HTTP request, just continue the chain
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        logger.info("LoggingFilter destroyed");
    }

    /**
     * Generate a unique ID for the request
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Set up the logging context with information from the request
     */
    private void setupLoggingContext(HttpServletRequest request, String requestId) {
        // Add the request ID to the logging context
        org.slf4j.MDC.put("requestId", requestId);

        // Add the request URL and method
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        String fullUrl = queryString != null ? url + "?" + queryString : url;

        org.slf4j.MDC.put("url", url);
        org.slf4j.MDC.put("method", method);

        // Add session info if available
        HttpSession session = request.getSession(false);
        if (session != null) {
            org.slf4j.MDC.put("sessionId", session.getId());

            // Add user info if available
            User user = (User) session.getAttribute("user");
            if (user != null) {
                String userId = String.valueOf(user.getUserId());
                String userRole = user.getRoleName();

                logger.setUserContext(userId, userRole);

                // Log access event for authenticated requests
                String ipAddress = getClientIpAddress(request);
                logger.logAccess(userId, ipAddress, fullUrl, method);
            } else {
                // Log access event for unauthenticated requests
                String ipAddress = getClientIpAddress(request);
                logger.logAccess("anonymous", ipAddress, fullUrl, method);
            }
        } else {
            // Log access event for requests without session
            String ipAddress = getClientIpAddress(request);
            logger.logAccess("anonymous", ipAddress, fullUrl, method);
        }

        // Add IP address
        String ipAddress = getClientIpAddress(request);
        org.slf4j.MDC.put("ipAddress", ipAddress);

        // Add user agent
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            org.slf4j.MDC.put("userAgent", userAgent);
        }

        // Add referer if available
        String referer = request.getHeader("Referer");
        if (referer != null) {
            org.slf4j.MDC.put("referer", referer);
        }

        // Log the start of the request
        logger.info("Request started: {} {} (ID: {})", method, fullUrl, requestId);
    }

    /**
     * Extract the client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Clean up the logging context
     */
    private void cleanupLoggingContext() {
        org.slf4j.MDC.remove("requestId");
        org.slf4j.MDC.remove("url");
        org.slf4j.MDC.remove("method");
        org.slf4j.MDC.remove("sessionId");
        org.slf4j.MDC.remove("ipAddress");
        org.slf4j.MDC.remove("userAgent");
        org.slf4j.MDC.remove("referer");

        // Clear user context
        logger.clearUserContext();
    }

    /**
     * Log the completion of the request
     */
    private void logRequestCompletion(HttpServletRequest request, HttpServletResponse response, String timerId) {
        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        String fullUrl = queryString != null ? url + "?" + queryString : url;
        String method = request.getMethod();
        int status = response.getStatus();

        // Additional info for timer
        String additionalInfo = String.format("status=%d url=%s", status, fullUrl);

        // Stop the timer and log the execution time
        logger.stopTimer(timerId, additionalInfo);

        // Log additional information for error responses
        if (status >= 400) {
            logger.warn("Request completed with error: {} {} - Status: {}", method, fullUrl, status);

            // Log specific categories of errors
            if (status == 404) {
                logger.warn("Resource not found: {}", fullUrl);
            } else if (status == 403) {
                logger.warn("Access forbidden: {}", fullUrl);
            } else if (status >= 500) {
                logger.error("Server error processing request: {} {}", method, fullUrl);
            }
        }
    }
}