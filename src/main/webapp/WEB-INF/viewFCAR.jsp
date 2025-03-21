<%@ page import="java.util.List" %>
<%@ page import="com.ABETAppTeam.FCAR" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>View FCARs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<h1>Faculty Course Assessment Reports (FCARs)</h1>
<a href="${pageContext.request.contextPath}/ProfessorServlet" class="btn">Back to Dashboard</a>

<div class="section">
    <h2>Existing FCARs</h2>
    <c:choose>
        <c:when test="${not empty allFCARs}">
            <ul>
                <c:forEach var="fcar" items="${allFCARs}">
                    <li>
                        <strong>Professor:</strong> <c:out value="${fcar.professorId}"/> -
                        <strong>Course:</strong> <c:out value="${fcar.courseId}"/> -
                        <strong>Semester:</strong> <c:out value="${fcar.semester}"/> -
                        <strong>Year:</strong> <c:out value="${fcar.year}"/> -
                        <strong>Status:</strong> <c:out value="${fcar.status}"/>
                    </li>
                </c:forEach>
            </ul>
        </c:when>
        <c:otherwise>
            <p>No FCARs available.</p>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
