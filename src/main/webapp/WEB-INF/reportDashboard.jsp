<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ABET Assessment Reports</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <!-- Include any needed JavaScript libraries like Chart.js for data visualization -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<jsp:include page="header.jsp" />

<div class="container">
    <h1>ABET Assessment Reports</h1>

    <!-- Display any error messages -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <!-- Display any success messages -->
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>

    <div class="row">
        <div class="col-md-3">
            <div class="card">
                <div class="card-header">Generate Reports</div>
                <div class="card-body">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link" href="#fullReport" data-toggle="tab">Full Report</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#semesterReport" data-toggle="tab">Semester Report</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#courseReport" data-toggle="tab">Course Reports</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#outcomeReport" data-toggle="tab">Outcome Report</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#trendReport" data-toggle="tab">Trend Report</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="col-md-9">
            <div class="tab-content">
                <!-- Full Report Form -->
                <div class="tab-pane fade" id="fullReport">
                    <div class="card">
                        <div class="card-header">Generate Full Report</div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/ReportServlet" method="get">
                                <input type="hidden" name="action" value="generateFullReport">

                                <div class="form-group">
                                    <label for="reportTitle">Report Title</label>
                                    <input type="text" class="form-control" id="reportTitle" name="reportTitle"
                                           value="Full ABET Assessment Report">
                                </div>

                                <button type="submit" class="btn btn-primary">Generate Report</button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Semester Report Form -->
                <div class="tab-pane fade" id="semesterReport">
                    <div class="card">
                        <div class="card-header">Generate Semester Report</div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/ReportServlet" method="get">
                                <input type="hidden" name="action" value="generateSemesterReport">

                                <div class="form-group">
                                    <label for="reportTitleSemester">Report Title</label>
                                    <input type="text" class="form-control" id="reportTitleSemester" name="reportTitle"
                                           value="Semester ABET Assessment Report">
                                </div>

                                <div class="form-group">
                                    <label for="semester">Semester</label>
                                    <select class="form-control" id="semester" name="semester" required>
                                        <option value="">Select a semester</option>
                                        <c:forEach items="${semesters}" var="sem">
                                            <option value="${sem}">${sem}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label for="year">Year</label>
                                    <select class="form-control" id="year" name="year" required>
                                        <option value="">Select a year</option>
                                        <c:forEach items="${years}" var="yr">
                                            <option value="${yr}">${yr}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <button type="submit" class="btn btn-primary">Generate Report</button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Course Reports Form -->
                <div class="tab-pane fade" id="courseReport">
                    <div class="card">
                        <div class="card-header">Generate Course-Based Reports</div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/ReportServlet" method="get">
                                <input type="hidden" name="action" value="generateCourseReports">

                                <div class="form-group">
                                    <label for="reportTitleCourse">Report Title</label>
                                    <input type="text" class="form-control" id="reportTitleCourse" name="reportTitle"
                                           value="Course-Based ABET Assessment Reports">
                                </div>

                                <button type="submit" class="btn btn-primary">Generate Reports</button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Outcome Report Form -->
                <div class="tab-pane fade" id="outcomeReport">
                    <div class="card">
                        <div class="card-header">Generate Outcome-Focused Report</div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/ReportServlet" method="get">
                                <input type="hidden" name="action" value="generateOutcomeReport">

                                <div class="form-group">
                                    <label for="reportTitleOutcome">Report Title</label>
                                    <input type="text" class="form-control" id="reportTitleOutcome" name="reportTitle"
                                           value="Outcome-Focused ABET Assessment Report">
                                </div>

                                <div class="form-group">
                                    <label>Select Outcomes to Include</label>
                                    <div class="outcome-checkboxes">
                                        <c:forEach items="${outcomes}" var="outcome">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="outcomeIds"
                                                       value="${outcome.id}" id="outcome${outcome.id}">
                                                <label class="form-check-label" for="outcome${outcome.id}">
                                                        ${outcome.name} - ${outcome.description}
                                                </label>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary">Generate Report</button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Trend Report Form -->
                <div class="tab-pane fade" id="trendReport">
                    <div class="card">
                        <div class="card-header">Generate Trend Report</div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/ReportServlet" method="get">
                                <input type="hidden" name="action" value="generateTrendReport">

                                <div class="form-group">
                                    <label for="reportTitleTrend">Report Title</label>
                                    <input type="text" class="form-control" id="reportTitleTrend" name="reportTitle"
                                           value="Trend ABET Assessment Report">
                                </div>

                                <div class="form-group">
                                    <label for="startYear">Start Year</label>
                                    <select class="form-control" id="startYear" name="startYear" required>
                                        <option value="">Select start year</option>
                                        <c:forEach items="${years}" var="yr">
                                            <option value="${yr}">${yr}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label for="endYear">End Year</label>
                                    <select class="form-control" id="endYear" name="endYear" required>
                                        <option value="">Select end year</option>
                                        <c:forEach items="${years}" var="yr">
                                            <option value="${yr}">${yr}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <button type="submit" class="btn btn-primary">Generate Report</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

<script>
    // Activate the first tab by default
    document.addEventListener('DOMContentLoaded', function() {
        document.querySelector('.nav-link').click();
    });

    // Tab switching functionality
    document.querySelectorAll('.nav-link').forEach(function(navLink) {
        navLink.addEventListener('click', function(e) {
            e.preventDefault();

            // Hide all tab panes
            document.querySelectorAll('.tab-pane').forEach(function(pane) {
                pane.classList.remove('show', 'active');
            });

            // Deactivate all nav links
            document.querySelectorAll('.nav-link').forEach(function(link) {
                link.classList.remove('active');
            });

            // Show the selected tab pane
            const targetId = this.getAttribute('href').substring(1);
            const targetPane = document.getElementById(targetId);
            targetPane.classList.add('show', 'active');

            // Activate the clicked nav link
            this.classList.add('active');
        });
    });
</script>
</body>
</html>