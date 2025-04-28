<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ABETAppTeam.model.Outcome" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Outcome-Based Reports</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="dashboard" id="outcomeReportsDashboard">
    <div class="header-container">
        <h1>Outcome-Based Assessment Reports</h1>
    </div>

    <!-- Generate Reports Section -->
    <div class="section">
        <h2>Generate Report</h2>
        <p>Select an outcome and report type. If "Academic Year" is chosen, select a year.</p>
        <form action="${pageContext.request.contextPath}/ReportServlet" method="get" class="form-inline">
            <div class="form-group">
                <label for="outcomeId">Outcome:</label>
                <select id="outcomeId" name="outcomeId" class="form-control" required>
                    <option value="">-- Select Outcome --</option>
                    <c:forEach var="o" items="${outcomes}">
                        <option value="${o.id}">${o.id} - ${o.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="reportScope">Report Scope:</label>
                <select id="reportScope" name="scope" class="form-control" required onchange="toggleYearSelect()">
                    <option value="fullCycle">Full Cycle</option>
                    <option value="academicYear">Academic Year</option>
                </select>
            </div>
            <div class="form-group" id="yearGroup" style="display:none;">
                <label for="year">Year:</label>
                <select id="year" name="year" class="form-control">
                    <option value="">-- Select Year --</option>
                    <c:forEach var="yr" items="${years}">
                        <option value="${yr}">${yr}</option>
                    </c:forEach>
                </select>
            </div>
            <input type="hidden" name="action" value="generateOutcomeReport" />
            <button type="submit" class="btn">Generate</button>
        </form>
    </div>

    <!-- Display Generated Reports -->
    <div class="section">
        <h2>Available Outcome Reports</h2>
        <div class="report-list">
            <c:choose>
                <c:when test="${not empty reports}">
                    <c:forEach var="r" items="${reports}">
                        <div class="report-item">
                            <span class="report-title">${r.reportTitle}</span>
                            <span class="report-date">${r.generatedDate}</span>
                            <div class="report-actions">
                                <form action="${pageContext.request.contextPath}/ReportServlet" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="downloadOutcomeReport" />
                                    <input type="hidden" name="id" value="${r.reportId}" />
                                    <button type="submit" class="btn">Download (.xlsx)</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/ReportServlet" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="viewOutcomeReport" />
                                    <input type="hidden" name="id" value="${r.reportId}" />
                                    <button type="submit" class="btn">View</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p>No reports found. Generate one above.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/footer.jsp" />

<script>
    function toggleYearSelect() {
        const scope = document.getElementById('reportScope').value;
        document.getElementById('yearGroup').style.display = (scope === 'academicYear') ? 'block' : 'none';
    }
    document.addEventListener('DOMContentLoaded', toggleYearSelect);
</script>
</body>
</html>
