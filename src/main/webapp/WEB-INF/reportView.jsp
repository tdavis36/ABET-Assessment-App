<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${report.reportTitle} - ABET Assessment</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<jsp:include page="header.jsp" />

<div class="container">
    <div class="report-header">
        <h1>${report.reportTitle}</h1>
        <p>Report ID: ${report.reportId}</p>
        <p>
            <c:if test="${not empty report.semester}">
                Semester: ${report.semester} ${report.year}
            </c:if>
        </p>
        <p>Generated: <fmt:formatDate value="${generatedAt}" pattern="MMMM d, yyyy h:mm a" /></p>
    </div>

    <div class="report-actions">
        <div class="btn-group">
            <button class="btn btn-primary" onclick="window.print()">Print Report</button>
            <a href="${pageContext.request.contextPath}/ReportServlet?action=exportReport&reportId=${report.reportId}&format=PDF"
               class="btn btn-secondary">Export as PDF</a>
            <a href="${pageContext.request.contextPath}/ReportServlet?action=exportReport&reportId=${report.reportId}&format=CSV"
               class="btn btn-secondary">Export as CSV</a>
        </div>
    </div>

    <div class="report-summary">
        <div class="card">
            <div class="card-header">Report Summary</div>
            <div class="card-body">
                <p>Report Type: ${reportType}</p>
                <p>Total FCARs: ${report.fcarList.size()}</p>

                <c:if test="${reportType == 'Semester Report'}">
                    <p>Semester: ${semester} ${year}</p>
                </c:if>

                <c:if test="${reportType == 'Outcome-Focused Report'}">
                    <p>Targeted Outcomes:</p>
                    <ul>
                        <c:forEach items="${targetOutcomes}" var="outcomeId">
                            <li>${outcomeId} - ${outcomeDescriptions[outcomeId]}</li>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>
        </div>
    </div>

    <!-- Course Performance Chart -->
    <div class="row">
        <div class="col-md-12">
            <div class="card">
                <div class="card-header">Course Performance</div>
                <div class="card-body">
                    <canvas id="coursePerformanceChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Outcome Statistics -->
    <div class="row">
        <div class="col-md-12">
            <div class="card">
                <div class="card-header">Outcome Achievement</div>
                <div class="card-body">
                    <canvas id="outcomeChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- FCARs Table -->
    <div class="row">
        <div class="col-md-12">
            <div class="card">
                <div class="card-header">Included FCARs</div>
                <div class="card-body">
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th>FCAR ID</th>
                            <th>Course</th>
                            <th>Instructor</th>
                            <th>Semester</th>
                            <th>Year</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${report.fcarList}" var="fcar">
                            <tr>
                                <td>${fcar.fcarId}</td>
                                <td>${fcar.courseCode}</td>
                                <td>${fcar.instructorId}</td>
                                <td>${fcar.semester}</td>
                                <td>${fcar.year}</td>
                                <td>${fcar.status}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

<script>
    // Course Performance Chart
    const courseCtx = document.getElementById('coursePerformanceChart').getContext('2d');
    const courseLabels = [];
    const courseData = [];

    <c:forEach items="${coursePerformance}" var="entry">
    courseLabels.push("${entry.key}");
    courseData.push(${entry.value});
    </c:forEach>

    new Chart(courseCtx, {
        type: 'bar',
        data: {
            labels: courseLabels,
            datasets: [{
                label: 'Course Performance',
                data: courseData,
                backgroundColor: 'rgba(54, 162, 235, 0.5)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    title: {
                        display: true,
                        text: 'Achievement (%)'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Course Code'
                    }
                }
            }
        }
    });

    // Outcome Achievement Chart
    const outcomeCtx = document.getElementById('outcomeChart').getContext('2d');
    const outcomeLabels = [];
    const outcomeData = [];

    <c:forEach items="${outcomeStatistics}" var="entry">
    outcomeLabels.push("${entry.key}");
    outcomeData.push(${entry.value});
    </c:forEach>

    new Chart(outcomeCtx, {
        type: 'bar',
        data: {
            labels: outcomeLabels,
            datasets: [{
                label: 'Outcome Achievement',
                data: outcomeData,
                backgroundColor: 'rgba(75, 192, 192, 0.5)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    title: {
                        display: true,
                        text: 'Achievement (%)'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Outcome'
                    }
                }
            }
        }
    });
</script>
</body>
</html>