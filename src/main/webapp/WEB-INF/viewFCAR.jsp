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
    <!-- Add custom styles to override button width -->
    <style>
        /* Override button width to prevent full-width on all screens */
        .btn {
            width: auto !important;
            max-width: none !important;
        }

        /* Specific styling for the toggle-details button */
        .toggle-details {
            width: auto !important;
            display: inline-block !important;
            margin-left: auto !important;
            flex-grow: 0 !important;
        }

        /* Maintain spacing between buttons */
        .fcar-actions {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            justify-content: flex-end;
        }

        /* Ensure buttons remain a reasonable size on mobile */
        @media (max-width: 768px) {
            .btn {
                padding: 8px 12px;
                font-size: 14px;
            }

            /* Improve responsive layout for action buttons */
            .fcar-actions {
                justify-content: flex-start;
            }

            /* Ensure the fcar-header stays as a row even on mobile */
            .fcar-header {
                flex-wrap: nowrap !important;
                gap: 10px;
            }

            /* Keep toggle button from growing */
            .toggle-details {
                flex-shrink: 0;
                flex-basis: auto;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <h1>FCAR Details</h1>

    <%
        // Retrieve the authenticated user from the session
        User user = (User) session.getAttribute("user");
        String dashboardUrl = "";

        // Determine dashboard URL based on a user role
        if (user instanceof Admin) {
            dashboardUrl = request.getContextPath() + "/AdminServlet";
        } else if (user instanceof Professor) {
            dashboardUrl = request.getContextPath() + "/ProfessorServlet";
        } else {
            // Fallback to index if no specific role found
            dashboardUrl = request.getContextPath() + "/index";
        }

        // Load FCARs from the database based on a user role
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
                            <!-- Header with title and basic info -->
                            <div class="fcar-header" style="display: flex; justify-content: space-between; align-items: center;">
                                <div>
                                    <h3>FCAR #${status.index + 1}: ${fcar.courseCode}</h3>
                                    <div>
                                        <strong>Professor ID:</strong> ${fcar.instructorId} |
                                        <strong>Semester:</strong> ${fcar.semester} ${fcar.year} |
                                        <strong>Status:</strong>
                                        <span class="status-badge status-${fn:toLowerCase(fcar.status)}">${fcar.status}</span>
                                    </div>
                                </div>
                                <button class="btn toggle-details" style="width: auto !important; flex-shrink: 0;" onclick="toggleDetails('fcar-${fcar.fcarId}')">Show Details</button>
                            </div>

                            <!-- Details section that toggles visibility -->
                            <div id="fcar-${fcar.fcarId}" class="fcar-details" style="display: none; margin-top: 10px;">
                                <!-- Actions Section - Moved to top for better visibility -->
                                <div class="fcar-section">
                                    <div class="fcar-actions">
                                        <!-- Only show edit button if the user is the author or an admin -->
                                        <c:if test="${user.userId == fcar.instructorId || user.roleId == 1}">
                                            <form method="get" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                                <input type="hidden" name="action" value="editFCAR"/>
                                                <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                                <button type="submit" class="btn">Edit FCAR</button>
                                            </form>
                                        </c:if>

                                        <!-- Only show approve/reject buttons for admins and submitted FCARs -->
                                        <c:if test="${user.roleId == 1 && fcar.status == 'Submitted'}">
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
                                    <h4>FCAR Information</h4>
                                    <p><strong>Course Code:</strong> ${fcar.courseCode}</p>
                                    <p><strong>Instructor ID:</strong> ${fcar.instructorId}</p>
                                    <p><strong>Semester/Year:</strong> ${fcar.semester} ${fcar.year}</p>
                                    <p><strong>Status:</strong> ${fcar.status}</p>
                                    <p><strong>Date Created:</strong> ${fcar.createdAt}</p>
                                    <c:if test="${not empty fcar.updatedAt}">
                                        <p><strong>Last Updated:</strong> ${fcar.updatedAt}</p>
                                    </c:if>
                                </div>

                                <!-- Student Learning Outcomes Section -->
                                <c:if test="${not empty fcar.outcomeId}">
                                    <div class="fcar-section">
                                        <h4>Student Learning Outcomes</h4>
                                        <p><strong>Outcome ID:</strong> ${fcar.outcomeId}</p>
                                        <p><strong>Indicator ID:</strong> ${fcar.indicatorId}</p>
                                    </div>
                                </c:if>

                                <!-- Assessment Methods Section -->
                                <c:if test="${not empty fcar.assessmentMethods}">
                                    <div class="fcar-section">
                                        <h4>Assessment Methods</h4>
                                        <c:forEach var="method" items="${fcar.assessmentMethods}">
                                            <p><strong>${method.key}:</strong> ${method.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>

                                <!-- Student Outcomes Section -->
                                <c:if test="${not empty fcar.studentOutcomes}">
                                    <div class="fcar-section">
                                        <h4>Student Achievement Levels</h4>
                                        <c:forEach var="outcome" items="${fcar.studentOutcomes}">
                                            <p><strong>${outcome.key}:</strong> ${outcome.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>

                                <!-- Improvement Actions Section -->
                                <c:if test="${not empty fcar.improvementActions}">
                                    <div class="fcar-section">
                                        <h4>Improvement Actions</h4>
                                        <c:forEach var="action" items="${fcar.improvementActions}">
                                            <p><strong>${action.key}:</strong> ${action.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>
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

    <form action="${pageContext.request.contextPath}/ViewFCARServlet" method="post" id="fcarForm">
        <input type="hidden" name="action" value="saveFCAR" id="actionInput"/>
        <!-- Rest of your form content... -->
    </form>

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