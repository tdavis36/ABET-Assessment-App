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

    <!-- Outstanding Tasks -->
    <div class="section">
        <h2>Outstanding Tasks</h2>
        <div class="task-box">
            <c:choose>
                <c:when test="${not empty tasks}">
                    <c:forEach var="task" items="${tasks}">
                        <c:if test="${task.status ne 'Completed'}">
                            <div class="task-item">
                                <div>
                                    <span class="status ${task.status}"></span>
                                    <c:out value="${task.taskName}"/>
                                </div>
                                <div class="task-actions">
                                    <span class="uploaded-docs">(0% completed)</span>

                                    <!-- Open Task -->
                                    <form method="get" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                        <input type="hidden" name="action" value="openTask"/>
                                        <input type="hidden" name="taskId" value="${task.taskId}"/>
                                        <button class="btn" type="submit">Open</button>
                                    </form>

                                    <!-- Submit Task -->
                                    <form method="post" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                        <input type="hidden" name="action" value="submitTask"/>
                                        <input type="hidden" name="taskId" value="${task.taskId}"/>
                                        <button class="btn" type="submit">Submit</button>
                                    </form>

                                    <!-- Complete Task -->
                                    <form method="post" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                        <input type="hidden" name="action" value="completeTask"/>
                                        <input type="hidden" name="taskId" value="${task.taskId}"/>
                                        <button class="btn" type="submit">Complete</button>
                                    </form>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p>No outstanding tasks.</p>
                </c:otherwise>
            </c:choose>

            <!-- Dummy Tasks for UI Testing -->
            <div class="task-item">
                <div><span class="status not-started"></span> Research Paper Review</div>
                <span class="uploaded-docs">document.docx (0% completed)</span>
            </div>
            <div class="task-item">
                <div><span class="status in-progress"></span> Midterm Exam Grading</div>
                <span class="uploaded-docs">grades.xlsx (50% completed)</span>
            </div>
        </div>
    </div>

    <!-- Completed Tasks -->
    <div class="section">
        <h2>Completed Tasks</h2>
        <div class="task-box">
            <c:choose>
                <c:when test="${not empty tasks}">
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
                </c:when>
                <c:otherwise>
                    <p>No completed tasks.</p>
                </c:otherwise>
            </c:choose>

            <!-- Dummy Completed Tasks for UI Testing -->
            <div class="task-item">
                <div><span class="status submitted"></span> Semester Course Summary</div>
                <span class="uploaded-docs">summary.docx (100% completed)</span>
            </div>
            <div class="task-item">
                <div><span class="status completed"></span> Final Research Paper</div>
                <span class="uploaded-docs">final_report.pdf (100% completed)</span>
            </div>
        </div>
    </div>

    <!-- FCAR Section -->
    <div class="section">
        <h2>Faculty Course Assessment Reports (FCARs)</h2>
        <div style="display: flex; gap: 10px; margin-bottom: 20px;">
            <form action="${pageContext.request.contextPath}/ProfessorServlet" method="get">
                <input type="hidden" name="action" value="createFCARForm"/>
                <button type="submit" class="btn">Create Comprehensive FCAR</button>
            </form>
            
            <form action="${pageContext.request.contextPath}/ProfessorServlet" method="get">
                <input type="hidden" name="action" value="createFakeFCARForm"/>
                <button type="submit" class="btn">Create Simple FCAR</button>
            </form>
            
            <form action="${pageContext.request.contextPath}/ProfessorServlet" method="get">
                <input type="hidden" name="action" value="viewFCARs"/>
                <button type="submit" class="btn">View FCARs</button>
            </form>
        </div>
        <p>Use the Comprehensive FCAR form to enter detailed assessment data for student outcomes and indicators.</p>
    </div>
</div>
</body>
</html>
