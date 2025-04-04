<%@ page import="java.util.Collection" %>
<%@ page import="com.ABETAppTeam.Report" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>View Reports</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        /* Example styling */
        .report-list {
            list-style-type: none;
            padding: 0;
        }
        .report-item {
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 20px;
            padding: 15px;
            background-color: #f9f9f9;
        }
        .report-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            padding-bottom: 10px;
            border-bottom: 1px solid #ddd;
        }
        .report-title {
            font-weight: bold;
            font-size: 1.2em;
        }
    </style>
</head>
<body>
    <h1>Faculty Course Assessment Reports (Reports)</h1>
    <div style="display: flex; gap: 10px; margin-bottom: 20px;">
        <a href="${pageContext.request.contextPath}/ProfessorServlet" class="btn">Back to Professor Dashboard</a>
        <a href="${pageContext.request.contextPath}/AdminServlet" class="btn">Back to Admin Dashboard</a>
    </div>

    <div class="section">
        <h2>Existing Reports</h2>
        <c:choose>
            <c:when test="${not empty allReports}">
                <ul class="report-list">
                    <c:forEach var="report" items="${allReports}" varStatus="status">
                        <li class="report-item">
                            <div class="report-header">
                                <div>
                                    <span class="report-title">
                                        Report #${status.index + 1}: ${report.reportTitle}
                                    </span>
                                </div>
                                <!-- Example: Button to view more details or export the report -->
                                <button onclick="alert('Export PDF for Report ID: ${report.reportId}');">
                                    Export PDF
                                </button>
                            </div>
                            
                            <!-- Example details for the report -->
                            <p><strong>Semester:</strong> ${report.semester}</p>
                            <p><strong>Year:</strong> ${report.year}</p>
                            
                            <!-- If you have FCARs in this report, show them -->
                            <c:if test="${not empty report.fcarList}">
                                <h3>FCARs in this Report:</h3>
                                <ul>
                                    <c:forEach var="fcar" items="${report.fcarList}">
                                        <li>FCAR ID: ${fcar.fcarId}, Course: ${fcar.courseId}, Semester: ${fcar.semester}, Year: ${fcar.year}</li>
                                    </c:forEach>
                                </ul>
                            </c:if>
                            
                            <!-- You can add more sections for Indicators, Outcomes, etc. -->
                            
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <p>No Reports available.</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
