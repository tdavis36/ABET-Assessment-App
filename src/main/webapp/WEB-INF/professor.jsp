<%@ page import="java.util.List" %>
<%@ page import="com.ABETAppTeam.Task" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Professor Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        /* Status Colors */
        .not-started { color: gray; }
        .in-progress { color: yellow; }
        .submitted { color: blue; }
        .completed { color: green; }

        .task-box { border: 1px solid #ddd; padding: 10px; margin-bottom: 10px; }
        .task-item { display: flex; justify-content: space-between; padding: 8px; }
        .task-actions button { margin-left: 5px; }
    </style>
</head>
<body>
<div class="dashboard">
    <h1>Welcome, Dr. <c:out value="${sessionScope.professorName}"/></h1>
    <a href="${pageContext.request.contextPath}/" class="btn">Logout</a>

    <!-- Status Key -->
    <div class="status-key">
        <div><span class="status not-started"></span> Not Started</div>
        <div><span class="status in-progress"></span> In Progress</div>
        <div><span class="status submitted"></span> Submitted</div>
        <div><span class="status completed"></span> Completed</div>
    </div>

    <!-- FCAR Section -->
    <div class="section">
        <h2>Faculty Course Assessment Reports (FCARs)</h2>
        <div style="display: flex; gap: 10px; margin-bottom: 20px;">
            <form action="${pageContext.request.contextPath}/ProfessorServlet" method="get">
                <input type="hidden" name="action" value="createFCARForm"/>
                <button type="submit" class="btn">Create FCAR</button>
            </form>
            
            <form action="${pageContext.request.contextPath}/ProfessorServlet" method="get">
                <input type="hidden" name="action" value="viewFCARs"/>
                <button type="submit" class="btn">View FCARs</button>
            </form>
        </div>
        <p>Use the FCAR form to enter detailed assessment data for student outcomes and indicators.</p>
    </div>

    <!-- All FCARs Section -->
    <div class="section">
        <h2>All FCARs</h2>
        <div class="task-box">
            <c:choose>
                <c:when test="${not empty assignedFCARs}">
                    <c:forEach var="fcar" items="${assignedFCARs}">
                        <div class="task-item">
                            <div>
                                <span class="status ${fcar.status}"></span>
                                <strong>Professor:</strong> <c:out value="${fcar.professorId}"/> -
                                <strong>Course:</strong> <c:out value="${fcar.courseId}"/> - 
                                <strong>Semester:</strong> <c:out value="${fcar.semester}"/> <c:out value="${fcar.year}"/> -
                                <strong>Status:</strong> <c:out value="${fcar.status}"/>
                            </div>
                            <div class="task-actions">
                                <form method="get" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                    <input type="hidden" name="action" value="editFCAR"/>
                                    <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                    <button class="btn" type="submit">Open/Edit</button>
                                </form>

                                <form method="post" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                    <input type="hidden" name="action" value="submitFCARStatus"/>
                                    <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                    <button class="btn" type="submit">Submit</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p>No FCARs available.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
</body>
</html>
