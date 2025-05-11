<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ABETAppTeam.model.Report" %>
<%@ page import="com.ABETAppTeam.model.Outcome" %>
<%@ page import="com.ABETAppTeam.model.Course" %>
<%@ page import="com.ABETAppTeam.repository.CourseRepository" %>
<%@ page import="com.ABETAppTeam.repository.OutcomeRepository" %>
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
        <form action="${pageContext.request.contextPath}/reports" method="get" class="action-buttons form-inline">
            <!-- Outcome Selection -->
            <div class="form-group">
                <label for="outcomeSelect">Outcome:</label>
                <select id="outcomeSelect" name="outcomeId" class="form-control" required>
                    <option value="">-- Select Outcome --</option>
                    <c:forEach var="outcome" items="${outcomes}">
                        <option value="${outcome.id}">${outcome.id} - ${outcome.description}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Indicator Dropdown -->
            <div class="form-group">
                <label for="indicatorSelect">Indicator:</label>
                <select id="indicatorSelect" name="indicatorId" class="form-control">
                    <option value="">All Indicators</option>
                </select>
            </div>

            <!-- Course Dropdown -->
            <div class="form-group">
                <label for="courseSelect">Course:</label>
                <select id="courseSelect" name="courseId" class="form-control">
                    <option value="">All Courses</option>
                    <c:forEach var="course" items="${courses}">
                        <option value="${course.courseId}">${course.courseCode} - ${course.courseName}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Year Selection -->
            <div class="form-group" id="yearGroup">
                <label for="yearSelect">Year:</label>
                <select id="yearSelect" name="year" class="form-control">
                    <option value="">All Years</option>
                    <c:forEach var="yr" items="${years}">
                        <option value="${yr}">${yr}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Academic Year vs Full Cycle -->
            <div class="form-group">
                <label for="reportScope">Scope:</label>
                <select id="reportScope" name="scope" class="form-control" required onchange="toggleYearField()">
                    <option value="academicYear">Academic Year</option>
                    <option value="fullCycle">Full Cycle</option>
                </select>
            </div>

            <input type="hidden" id="actionField" name="action" value="generateOutcomeAcademicYearReport" />
            <button type="submit" class="btn">Generate</button>
        </form>
    </div>

    <!-- Available Reports Section -->
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
                                <form action="${pageContext.request.contextPath}/reports" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="downloadOutcomeReport" />
                                    <input type="hidden" name="reportId" value="${r.reportId}" />
                                    <button type="submit" class="btn">Download (.xlsx)</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/reports" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="viewOutcomeReport" />
                                    <input type="hidden" name="reportId" value="${r.reportId}" />
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

<script>
    // Populate indicator dropdown
    const indicatorsByOutcome = {};

    <c:if test="${not empty outcomes}">
    <c:forEach items="${outcomes}" var="outcome">
    indicatorsByOutcome['${outcome.id}'] = [];
    <c:if test="${not empty indicatorsByOutcome[outcome.id]}">
    <c:forEach items="${indicatorsByOutcome[outcome.id]}" var="ind" varStatus="st">
    indicatorsByOutcome['${outcome.id}'].push({
        id: '${ind.indicatorId}',
        description: '${ind.description}'
    });
    </c:forEach>
    </c:if>
    </c:forEach>
    </c:if>

    const outcomeSelect = document.getElementById('outcomeSelect');
    const indicatorSelect = document.getElementById('indicatorSelect');
    const reportScope = document.getElementById('reportScope');
    const actionField = document.getElementById('actionField');
    const yearGroup = document.getElementById('yearGroup');

    function updateIndicators() {
        const selected = outcomeSelect.value;
        indicatorSelect.innerHTML = '<option value="">All Indicators</option>';
        if (selected && indicatorsByOutcome[selected]) {
            indicatorsByOutcome[selected].forEach(ind => {
                const opt = document.createElement('option');
                opt.value = ind.id;
                opt.textContent = ind.id + ' - ' + ind.description;
                indicatorSelect.appendChild(opt);
            });
        }
    }

    function toggleYearField() {
        if (reportScope.value === 'academicYear') {
            yearGroup.style.display = 'inline-block';
            actionField.value = 'generateOutcomeAcademicYearReport';
        } else {
            yearGroup.style.display = 'none';
            actionField.value = 'generateOutcomeFullCycleReport';
        }
    }

    outcomeSelect.addEventListener('change', updateIndicators);
    reportScope.addEventListener('change', toggleYearField);
    document.addEventListener('DOMContentLoaded', () => {
        updateIndicators();
        toggleYearField();
    });
</script>
</body>
</html>