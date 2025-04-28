<%@ page import="java.util.List" %>
<%@ page import="com.ABETAppTeam.model.FCAR" %>
<%@ page import="com.ABETAppTeam.repository.FCARRepository" %>
<%@ page import="com.ABETAppTeam.model.User" %>
<%@ page import="com.ABETAppTeam.model.Admin" %>
<%@ page import="com.ABETAppTeam.model.Professor" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>FCAR View</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        /* Ensure buttons auto-size */
        .btn { width: auto !important; max-width: none !important; }
        .fcar-actions { display: flex; flex-wrap: wrap; gap: 8px; justify-content: flex-end; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="dashboard" id="fcarViewDashboard">
    <div class="header-container">
        <h1>FCAR Details</h1>
    </div>

    <div class="section">
        <h2>Existing FCARs</h2>
        <%
            User user = (User) session.getAttribute("user");
            String dashboardUrl;
            if (user instanceof Admin) dashboardUrl = request.getContextPath() + "/AdminServlet";
            else if (user instanceof Professor) dashboardUrl = request.getContextPath() + "/ProfessorServlet";
            else dashboardUrl = request.getContextPath() + "/index";

            FCARRepository repo = new FCARRepository();
            List<FCAR> allFCARs = (user instanceof Admin)
                    ? repo.findAll()
                    : repo.findByInstructorId(user.getUserId());
            request.setAttribute("allFCARs", allFCARs);
        %>
        <c:choose>
            <c:when test="${not empty allFCARs}">
                <ul class="fcar-list">
                    <c:forEach var="fcar" items="${allFCARs}" varStatus="status">
                        <li class="fcar-item">
                            <div class="fcar-header">
                                <div>
                                    <h3>FCAR #${status.index + 1}: ${fcar.courseCode}</h3>
                                    <p>
                                        <strong>Professor ID:</strong> ${fcar.instructorId} |
                                        <strong>Semester:</strong> ${fcar.semester} ${fcar.year} |
                                        <strong>Status:</strong>
                                        <span class="status-badge status-${fn:toLowerCase(fcar.status)}">${fcar.status}</span>
                                    </p>
                                </div>
                                <button class="btn toggle-details" onclick="toggleDetails('fcar-${fcar.fcarId}')">Show Details</button>
                            </div>
                            <div id="fcar-${fcar.fcarId}" class="fcar-details" style="display:none;">
                                <div class="fcar-section fcar-actions">
                                    <c:if test="${user.userId == fcar.instructorId || user.roleId == 1}">
                                        <form method="get" action="${pageContext.request.contextPath}/ProfessorServlet">
                                            <input type="hidden" name="action" value="editFCAR" />
                                            <input type="hidden" name="fcarId" value="${fcar.fcarId}" />
                                            <button type="submit" class="btn">Edit FCAR</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${user.roleId == 1 && fcar.status == 'Submitted'}">
                                        <form method="post" action="${pageContext.request.contextPath}/AdminServlet">
                                            <input type="hidden" name="action" value="approveFCAR" />
                                            <input type="hidden" name="fcarId" value="${fcar.fcarId}" />
                                            <button type="submit" class="btn">Approve</button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/AdminServlet">
                                            <input type="hidden" name="action" value="rejectFCAR" />
                                            <input type="hidden" name="fcarId" value="${fcar.fcarId}" />
                                            <button type="submit" class="btn btn-danger">Reject</button>
                                        </form>
                                    </c:if>
                                </div>
                                <div class="fcar-section">
                                    <h4>FCAR Information</h4>
                                    <p><strong>Course Code:</strong> ${fcar.courseCode}</p>
                                    <p><strong>Instructor ID:</strong> ${fcar.instructorId}</p>
                                    <p><strong>Semester/Year:</strong> ${fcar.semester} ${fcar.year}</p>
                                    <p><strong>Status:</strong> ${fcar.status}</p>
                                    <p><strong>Created:</strong> ${fcar.createdAt}</p>
                                    <c:if test="${not empty fcar.updatedAt}"><p><strong>Updated:</strong> ${fcar.updatedAt}</p></c:if>
                                </div>
                                <c:if test="${not empty fcar.outcomeId}">
                                    <div class="fcar-section">
                                        <h4>Learning Outcomes</h4>
                                        <p><strong>Outcome ID:</strong> ${fcar.outcomeId}</p>
                                        <p><strong>Indicator ID:</strong> ${fcar.indicatorId}</p>
                                    </div>
                                </c:if>
                                <c:if test="${not empty fcar.assessmentMethods}">
                                    <div class="fcar-section">
                                        <h4>Assessment Methods</h4>
                                        <c:forEach var="method" items="${fcar.assessmentMethods}">
                                            <p><strong>${method.key}:</strong> ${method.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <c:if test="${not empty fcar.studentOutcomes}">
                                    <div class="fcar-section">
                                        <h4>Student Achievement</h4>
                                        <c:forEach var="outcome" items="${fcar.studentOutcomes}">
                                            <p><strong>${outcome.key}:</strong> ${outcome.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <c:if test="${not empty fcar.improvementActions}">
                                    <div class="fcar-section">
                                        <h4>Improvement Actions</h4>
                                        <c:forEach var="act" items="${fcar.improvementActions}">
                                            <p><strong>${act.key}:</strong> ${act.value}</p>
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

    <div class="action-buttons">
        <form action="${dashboardUrl}" method="get">
            <button type="submit" class="btn">Back to Dashboard</button>
        </form>
    </div>
</div>

<script>
    function toggleDetails(id) {
        const el = document.getElementById(id);
        const btn = event.currentTarget;
        if (el.style.display === 'none') {
            el.style.display = 'block'; btn.textContent = 'Hide Details';
        } else {
            el.style.display = 'none'; btn.textContent = 'Show Details';
        }
    }
</script>
</body>
</html>
