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
</head>
<body>
<div class="dashboard">
    <!-- Example: retrieve the professor's name from session or user object -->
    <h1>Welcome, Dr. <c:out value="${sessionScope.professorName}"/></h1>
    <a href="${pageContext.request.contextPath}/" class="btn">Logout</a>

    <!-- Status Key -->
    <div class="status-key">
        <div><span class="status not-started"></span> Not Started</div>
        <div><span class="status in-progress"></span> In Progress</div>
        <div><span class="status submitted"></span> Submitted</div>
        <div><span class="status completed"></span> Completed</div>
    </div>

    <!-- Outstanding Tasks -->
    <div class="section">
        <h2>Outstanding Tasks</h2>
        <div class="task-box">
            <c:forEach var="task" items="${tasks}">
                <!-- Show tasks that are NOT completed -->
                <c:if test="${task.status ne 'Completed'}">
                    <div class="task-item">
                        <div>
                            <span class="status ${task.status}"></span>
                            <c:out value="${task.taskName}"/>
                        </div>
                        <div class="task-actions">
                            <!-- Example placeholder for doc progress -->
                            <span class="uploaded-docs">(0% completed)</span>

                            <!-- 'Open' could lead to a detail page or a form upload -->
                            <form method="get" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                <input type="hidden" name="action" value="openTask"/>
                                <input type="hidden" name="taskId" value="${task.taskId}"/>
                                <button class="btn" type="submit">Open</button>
                            </form>

                            <!-- 'Submit' updates the status to "Submitted" -->
                            <form method="post" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                <input type="hidden" name="action" value="submitTask"/>
                                <input type="hidden" name="taskId" value="${task.taskId}"/>
                                <button class="btn" type="submit">Submit</button>
                            </form>

                            <!-- (New) 'Complete' sets status to "Completed" -->
                            <form method="post" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                <input type="hidden" name="action" value="completeTask"/>
                                <input type="hidden" name="taskId" value="${task.taskId}"/>
                                <button class="btn" type="submit">Complete</button>
                            </form>
                        </div>
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </div>

    <!-- Button to create a Fake FCAR -->
    <form action="${pageContext.request.contextPath}/ProfessorServlet" method="get">
        <input type="hidden" name="action" value="createFakeFCARForm"/>
        <button type="submit">Create FCAR</button>
    </form>

    <!-- Completed Tasks -->
    <div class="section">
        <h2>Completed Tasks</h2>
        <div class="task-box">
            <c:forEach var="task" items="${tasks}">
                <c:if test="${task.status eq 'Completed'}">
                    <div class="task-item">
                        <div>
                            <span class="status completed"></span>
                            <c:out value="${task.taskName}"/>
                        </div>
                        <span class="uploaded-docs">(100% completed)</span>
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </div>
</div>
</body>
</html>
