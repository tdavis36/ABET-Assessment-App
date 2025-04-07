<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Professor Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .fcar-box { border: 1px solid #ddd; padding: 10px; margin-bottom: 10px; }
        .fcar-item { display: flex; justify-content: space-between; padding: 8px; }
        .fcar-actions button { margin-left: 5px; }
    </style>
</head>
<body>
<div class="dashboard">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h1>Welcome, Dr. <c:out value="${sessionScope.professorName}"/></h1>
        <a href="${pageContext.request.contextPath}/" class="btn" style="margin-left: 20px;">Logout</a>
    </div>

    <!-- Status Key -->
    <div class="status-key">
        <div><span class="status draft"></span> Draft</div>
        <div><span class="status submitted"></span> Submitted</div>
        <div><span class="status approved"></span> Approved</div>
        <div><span class="status rejected"></span> Rejected</div>
    </div>

    <!-- FCAR Section -->
    <div class="section">
        <h2>Faculty Course Assessment Reports (FCARs)</h2>
        <div style="display: flex; gap: 10px; margin-bottom: 20px;">
            <form action="${pageContext.request.contextPath}/ProfessorServlet" method="get">
                <input type="hidden" name="action" value="viewFCARs"/>
                <button type="submit" class="btn">View FCARs</button>
            </form>
        </div>
        <p>Use the FCAR form to enter detailed assessment data for student outcomes and indicators.</p>
    </div>

    <!-- Assigned FCARs Section -->
    <div class="section">
        <h2>Your Assigned FCARs</h2>
        <div class="fcar-box">
            <c:choose>
                <c:when test="${not empty assignedFCARs}">
                    <c:forEach var="fcar" items="${assignedFCARs}">
                        <div class="fcar-item">
                            <div>
                                <span class="status ${fn:toLowerCase(fcar.status)}"></span>
                                <strong>Course:</strong> <c:out value="${fcar.courseId}"/> - 
                                <strong>Semester:</strong> <c:out value="${fcar.semester}"/> <c:out value="${fcar.year}"/> -
                                <strong>Status:</strong> <c:out value="${fcar.status}"/>
                            </div>
                            <div class="fcar-actions">
                                <form method="get" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                    <input type="hidden" name="action" value="editFCAR"/>
                                    <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                    <button class="btn" type="submit">Open/Edit</button>
                                </form>

                                <c:if test="${fcar.status != 'Submitted' && fcar.status != 'Approved'}">
                                    <form method="post" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                        <input type="hidden" name="action" value="submitFCARStatus"/>
                                        <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                        <button class="btn" type="submit">Submit</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p>No FCARs assigned to you.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
</body>
</html>
