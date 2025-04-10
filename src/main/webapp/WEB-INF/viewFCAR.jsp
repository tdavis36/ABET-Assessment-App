<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.ABETAppTeam.FCAR" %>
<%@ page import="com.ABETAppTeam.repository.FCARRepository" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ page import="com.ABETAppTeam.User" %>
<%@ page import="com.ABETAppTeam.Admin" %>
<%@ page import="com.ABETAppTeam.Professor" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>FCAR View</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="container">
    <h1>FCAR Details</h1>

    <%
        // Retrieve the authenticated user from the session
        User user = (User) session.getAttribute("user");
        String dashboardUrl = "";

        // Determine dashboard URL based on user role
        if (user instanceof Admin) {
            dashboardUrl = request.getContextPath() + "/AdminServlet";
        } else if (user instanceof Professor) {
            dashboardUrl = request.getContextPath() + "/ProfessorServlet";
        } else {
            // Fallback to index if no specific role found
            dashboardUrl = request.getContextPath() + "/index";
        }

        // Load FCARs from the database based on user role
        List<FCAR> allFCARs = null;
        FCARRepository fcarRepository = new FCARRepository();

        if (user instanceof Admin) {
            // Admin can see all FCARs
            allFCARs = fcarRepository.findAll();
        } else if (user instanceof Professor) {
            // Professor can only see their own FCARs
            allFCARs = fcarRepository.findByInstructorId(user.getUserId());
        }

        // Set the allFCARs attribute for JSTL access
        request.setAttribute("allFCARs", allFCARs);
    %>

    <div class="section">
        <h2>Existing FCARs</h2>
        <c:choose>
            <c:when test="${not empty allFCARs}">
                <ul class="fcar-list">
                    <c:forEach var="fcar" items="${allFCARs}" varStatus="status">
                        <li class="fcar-item">
                            <div class="fcar-header">
                                <div>
                                    <h3>FCAR #${status.index + 1}: ${fcar.courseId}</h3>
                                    <div>
                                        <strong>Professor:</strong> ${fcar.professorName} |
                                        <strong>Semester:</strong> ${fcar.semester} ${fcar.year} |
                                        <strong>Status:</strong>
                                        <span class="status-badge status-${fcar.status.toLowerCase()}">${fcar.status}</span>
                                    </div>
                                </div>
                                <button class="btn toggle-details" onclick="toggleDetails('fcar-${fcar.fcarId}')">Show Details</button>
                            </div>

                            <div id="fcar-${fcar.fcarId}" class="fcar-details" style="display: none;">
                                <!-- Actions Section - Moved to top for better visibility -->
                                <div class="fcar-section">
                                    <div class="fcar-actions" style="display: flex; justify-content: flex-end; gap: 10px; margin-bottom: 15px; border: none; padding-top: 0;">
                                        <!-- Only show edit button if the user is the author or an admin -->
                                        <c:if test="${user.userId == fcar.professorId || user.role == 'ADMIN'}">
                                            <form method="get" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                                <input type="hidden" name="action" value="editFCAR"/>
                                                <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                                <button type="submit" class="btn">Edit FCAR</button>
                                            </form>
                                        </c:if>

                                        <!-- Only show approve/reject buttons for admins and submitted FCARs -->
                                        <c:if test="${user.role == 'ADMIN' && fcar.status == 'Submitted'}">
                                            <form method="post" action="${pageContext.request.contextPath}/AdminServlet" style="display:inline;">
                                                <input type="hidden" name="action" value="approveFCAR"/>
                                                <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                                <button type="submit" class="btn" style="background-color: #28a745;">Approve</button>
                                            </form>
                                            <form method="post" action="${pageContext.request.contextPath}/AdminServlet" style="display:inline;">
                                                <input type="hidden" name="action" value="rejectFCAR"/>
                                                <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                                <button type="submit" class="btn" style="background-color: #dc3545;">Reject</button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>

                                <!-- FCAR Details Section -->
                                <div class="fcar-section">
                                    <h4>Course Information</h4>
                                    <p><strong>Course Description:</strong> ${fcar.courseDescription}</p>
                                    <p><strong>Enrollment:</strong> ${fcar.enrollment} students</p>
                                </div>

                                <!-- Display SLOs and assessment data -->
                                <div class="fcar-section">
                                    <h4>Student Learning Outcomes (SLOs)</h4>
                                    <c:forEach var="slo" items="${fcar.sloAssessments}">
                                        <div class="slo-item">
                                            <h5>SLO ${slo.sloId}: ${slo.description}</h5>
                                            <p><strong>Assessment Tool:</strong> ${slo.assessmentTool}</p>
                                            <p><strong>Achievement Target:</strong> ${slo.achievementTarget}%</p>
                                            <p><strong>Actual Achievement:</strong> ${slo.actualAchievement}%</p>
                                            <p><strong>Status:</strong>
                                                <span class="${slo.actualAchievement >= slo.achievementTarget ? 'success' : 'warning'}">
                                                        ${slo.actualAchievement >= slo.achievementTarget ? 'Target Met' : 'Target Not Met'}
                                                </span>
                                            </p>
                                        </div>
                                    </c:forEach>
                                </div>

                                <!-- Comments Section -->
                                <div class="fcar-section">
                                    <h4>Comments and Continuous Improvement</h4>
                                    <p>${fcar.comments}</p>
                                </div>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <p class="no-data">No FCARs found. Please create a new FCAR.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Add navigation button back to dashboard -->
    <div class="actions">
        <a href="<%= dashboardUrl %>" class="btn">Back to Dashboard</a>
    </div>

    <!-- JavaScript for expanding/collapsing FCAR details -->
    <script>
        function toggleDetails(id) {
            const details = document.getElementById(id);
            const button = event.currentTarget;

            if (details.style.display === "none") {
                details.style.display = "block";
                button.textContent = "Hide Details";
            } else {
                details.style.display = "none";
                button.textContent = "Show Details";
            }
        }
    </script>
</div>
</body>
</html>