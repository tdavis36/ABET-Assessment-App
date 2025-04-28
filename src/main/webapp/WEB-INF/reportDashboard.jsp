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
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="dashboard" id="reportsDashboard">
    <div class="header-container">
        <h1>Assessment Reports</h1>
    </div>

    <!-- Generate Outcome Reports -->
    <div class="section">
        <h2>Generate Outcome-Based Reports</h2>
        <div class="action-buttons">
            <!-- Academic Year Report -->
            <form action="${pageContext.request.contextPath}/ReportServlet" method="get" class="form-inline">
                <input type="hidden" name="action" value="generateOutcomeAcademicYearReport" />
                <div class="form-group">
                    <label for="outcomeIdYear">Outcome:</label>
                    <select id="outcomeIdYear" name="outcomeId" class="form-control" required>
                        <option value="">Select Outcome</option>
                        <c:forEach items="${outcomes}" var="o">
                            <option value="${o.id}">${o.id} - ${o.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="yearSelect">Year:</label>
                    <select id="yearSelect" name="year" class="form-control" required>
                        <option value="">Select Year</option>
                        <c:forEach items="${years}" var="yr">
                            <option value="${yr}">${yr}</option>
                        </c:forEach>
                    </select>
                </div>
                <button type="submit" class="btn">Generate Academic Year Report</button>
            </form>

            <!-- Full Cycle Report -->
            <form action="${pageContext.request.contextPath}/ReportServlet" method="get" class="form-inline">
                <input type="hidden" name="action" value="generateOutcomeFullCycleReport" />
                <div class="form-group">
                    <label for="outcomeIdCycle">Outcome:</label>
                    <select id="outcomeIdCycle" name="outcomeId" class="form-control" required>
                        <option value="">Select Outcome</option>
                        <c:forEach items="${outcomes}" var="o">
                            <option value="${o.id}">${o.id} - ${o.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <button type="submit" class="btn">Generate Full Cycle Report</button>
            </form>
        </div>
    </div>

    <!-- Display Generated Report Listings -->
    <div class="section">
        <h2>Available Reports</h2>
        <div class="report-list">
            <c:choose>
                <c:when test="${not empty allReports}">
                    <c:forEach var="report" items="${allReports}">
                        <div class="report-item">
                            <span class="report-title">${report.reportTitle}</span>
                            <div class="report-actions">
                                <form action="${pageContext.request.contextPath}/ReportServlet" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="downloadReport" />
                                    <input type="hidden" name="id" value="${report.reportId}" />
                                    <button type="submit" class="btn">Download (.xlsx)</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/ReportServlet" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="viewReport" />
                                    <input type="hidden" name="id" value="${report.reportId}" />
                                    <button type="submit" class="btn">View</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p>No reports available. Generate one above.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>
