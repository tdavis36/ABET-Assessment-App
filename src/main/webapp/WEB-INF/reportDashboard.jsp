<%@ page import="java.util.Collection" %>
<%@ page import="com.ABETAppTeam.model.Report" %>
<%@ page import="com.ABETAppTeam.model.Course" %>
<%@ page import="com.ABETAppTeam.repository.CourseRepository" %>
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
                        <c:forEach items="${csvOutcomes}" var="outcome">
                            <option value="${outcome.id}">${outcome.id} - ${outcome.description}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="indicatorId">Indicator:</label>
                    <select id="indicatorId" name="indicatorId" class="form-control">
                        <option value="">All Indicators</option>
                        <!-- Will be populated by JavaScript -->
                    </select>
                </div>

                <div class="form-group">
                    <label for="courseId">Course:</label>
                    <select id="courseId" name="courseId" class="form-control">
                        <option value="">All Courses</option>
                        <c:forEach items="${dbCourses}" var="course">
                            <option value="${course.courseId}">${course.courseCode} - ${course.courseName}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="yearSelect">Year:</label>
                    <select id="yearSelect" name="year" class="form-control" required>
                        <option value="">Select Year</option>
                        <option value="2020">2020</option>
                        <option value="2021">2021</option>
                        <option value="2022">2022</option>
                        <option value="2023">2023</option>
                        <option value="2024">2024</option>
                        <option value="2025">2025</option>
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
                        <c:forEach items="${csvOutcomes}" var="outcome">
                            <option value="${outcome.id}">${outcome.id} - ${outcome.description}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="indicatorIdCycle">Indicator:</label>
                    <select id="indicatorIdCycle" name="indicatorId" class="form-control">
                        <option value="">All Indicators</option>
                        <!-- Will be populated by JavaScript -->
                    </select>
                </div>

                <div class="form-group">
                    <label for="courseIdCycle">Course:</label>
                    <select id="courseIdCycle" name="courseId" class="form-control">
                        <option value="">All Courses</option>
                        <c:forEach items="${dbCourses}" var="course">
                            <option value="${course.courseId}">${course.courseCode} - ${course.courseName}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Year Range:</label>
                    <select name="startYear" class="form-control" required>
                        <option value="2020">2020</option>
                        <option value="2021">2021</option>
                        <option value="2022">2022</option>
                        <option value="2023">2023</option>
                        <option value="2024">2024</option>
                    </select>
                    <span>to</span>
                    <select name="endYear" class="form-control" required>
                        <option value="2021">2021</option>
                        <option value="2022">2022</option>
                        <option value="2023">2023</option>
                        <option value="2024">2024</option>
                        <option value="2025">2025</option>
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

<script>
    // Store indicator data by outcome
    const indicatorsByOutcome = {};

    // Initialize with data from the server
    <c:forEach items="${csvOutcomes}" var="outcome">
    indicatorsByOutcome["${outcome.id}"] = [
        <c:forEach items="${outcome.indicators}" var="indicator" varStatus="status">
        {id: "${indicator.id}", description: "${indicator.description}"}<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
    </c:forEach>

    // Function to update indicator dropdowns when outcome is selected
    function updateIndicators(outcomeSelectId, indicatorSelectId) {
        const outcomeSelect = document.getElementById(outcomeSelectId);
        const indicatorSelect = document.getElementById(indicatorSelectId);

        // Clear current options
        indicatorSelect.innerHTML = '<option value="">All Indicators</option>';

        const selectedOutcome = outcomeSelect.value;
        if (selectedOutcome && indicatorsByOutcome[selectedOutcome]) {
            indicatorsByOutcome[selectedOutcome].forEach(indicator => {
                const option = document.createElement('option');
                option.value = indicator.id;
                option.textContent = `${indicator.id} - ${indicator.description}`;
                indicatorSelect.appendChild(option);
            });
        }
    }

    // Add event listeners
    document.getElementById('outcomeIdYear').addEventListener('change', function() {
        updateIndicators('outcomeIdYear', 'indicatorId');
    });

    document.getElementById('outcomeIdCycle').addEventListener('change', function() {
        updateIndicators('outcomeIdCycle', 'indicatorIdCycle');
    });

    // Initialize indicators on page load
    document.addEventListener('DOMContentLoaded', function() {
        updateIndicators('outcomeIdYear', 'indicatorId');
        updateIndicators('outcomeIdCycle', 'indicatorIdCycle');
    });
</script>

<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>