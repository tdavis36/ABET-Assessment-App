<%@ page import="java.util.Collection" %>
<%@ page import="com.ABETAppTeam.model.Report" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Assessment Reports</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<c:set var="activePage" value="reports" scope="request"/>
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="dashboard" id="reportsDashboard">
    <div class="header-container">
        <h1>Assessment Reports</h1>
    </div>

    <!-- Generate Reports Section -->
    <div class="section">
        <h2>Generate Reports</h2>
        <div class="action-buttons">
            <form action="${pageContext.request.contextPath}/ReportServlet" method="get">
                <div class="form-group">
                    <label for="reportType">Report Type:</label>
                    <select id="reportType" name="action" class="form-control" required>
                        <option value="generateFullReport">Full Report</option>
                        <option value="generateSemesterReport">Semester Report</option>
                        <c:if test="${sessionScope.userRole == 'admin'}">
                            <option value="generateCustomReport">Custom Report</option>
                        </c:if>
                    </select>
                </div>
                <button type="submit" class="btn">Generate Report</button>
            </form>
        </div>
    </div>

    <!-- Existing Reports Section -->
    <div class="section">
        <h2>Existing Reports</h2>
        <div class="report-list">
            <c:choose>
                <c:when test="${not empty allReports}">
                    <c:forEach var="report" items="${allReports}">
                        <div class="report-item">
                            <div class="report-header">
                                <span class="report-title">${report.reportTitle}</span>
                                <span class="report-date">${report.semester} ${report.year}</span>
                            </div>
                            <div class="report-actions">
                                <!-- View Report -->
                                <form action="${pageContext.request.contextPath}/ReportServlet" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="viewReport" />
                                    <input type="hidden" name="id" value="${report.reportId}" />
                                    <button type="submit" class="btn">View</button>
                                </form>
                                <!-- Download PDF -->
                                <form action="${pageContext.request.contextPath}/ReportServlet" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="downloadReport" />
                                    <input type="hidden" name="id" value="${report.reportId}" />
                                    <button type="submit" class="btn">Download PDF</button>
                                </form>
                                <!-- Delete Report (admin only) -->
                                <c:if test="${sessionScope.userRole == 'admin'}">
                                    <form action="${pageContext.request.contextPath}/ReportServlet" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="deleteReport" />
                                        <input type="hidden" name="id" value="${report.reportId}" />
                                        <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to delete this report?');">Delete</button>
                                    </form>
                                </c:if>
                            </div>
                            <div class="report-details">
                                <strong>FCARs:</strong> ${fn:length(report.fcarList)}
                                <c:if test="${not empty report.description}">
                                    <p>${report.description}</p>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p>No reports available. Use the controls above to generate reports.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/reports.js"></script>
</body>
</html>
