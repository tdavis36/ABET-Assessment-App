<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Course-Based Reports - ABET Assessment</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="container">
  <div class="report-header">
    <h1>Course-Based ABET Assessment Reports</h1>
    <p>Generated: <fmt:formatDate value="${generatedAt}" pattern="MMMM d, yyyy h:mm a" /></p>
  </div>

  <div class="report-actions">
    <div class="btn-group">
      <button class="btn btn-primary" onclick="window.print()">Print Reports</button>
    </div>
  </div>

  <div class="course-selector">
    <div class="form-group">
      <label for="courseSelect">Select Course</label>
      <select class="form-control" id="courseSelect">
        <option value="">Overview (All Courses)</option>
        <c:forEach items="${courseCodes}" var="courseCode">
          <option value="${courseCode}">${courseCode}</option>
        </c:forEach>
      </select>
    </div>
  </div>

  <!-- Overview Section (shown when no specific course is selected) -->
  <div id="overviewSection" class="course-section">
    <h2>Overview of All Courses</h2>

    <!-- Combined Course Performance Chart -->
    <div class="card">
      <div class="card-header">Performance by Course</div>
      <div class="card-body">
        <canvas id="combinedCourseChart"></canvas>
      </div>
    </div>

    <!-- Courses Summary Table -->
    <div class="card mt-4">
      <div class="card-header">Courses Summary</div>
      <div class="card-body">
        <table class="table table-striped">
          <thead>
          <tr>
            <th>Course Code</th>
            <th>Number of FCARs</th>
            <th>Overall Performance</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${courseReports}" var="entry">
            <tr>
              <td>${entry.key}</td>
              <td>${entry.value.fcarList.size()}</td>
              <td>
                <c:set var="avgPerformance" value="0" />
                <c:forEach items="${entry.value.coursePerformanceData}" var="perf">
                  <c:set var="avgPerformance" value="${avgPerformance + perf.value}" />
                </c:forEach>
                <c:if test="${entry.value.coursePerformanceData.size() > 0}">
                  <fmt:formatNumber value="${avgPerformance / entry.value.coursePerformanceData.size()}"
                                    maxFractionDigits="1" />%
                </c:if>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- Individual Course Sections -->
  <c:forEach items="${courseReports}" var="entry">
    <div id="course${entry.key}" class="course-section" style="display: none;">
      <h2>Report for ${entry.key}</h2>

      <!-- Course Performance Chart -->
      <div class="card">
        <div class="card-header">Performance Indicators</div>
        <div class="card-body">
          <canvas id="chart${entry.key}"></canvas>
        </div>
      </div>

      <!-- Course FCARs -->
      <div class="card mt-4">
        <div class="card-header">Included FCARs</div>
        <div class="card-body">
          <table class="table table-striped">
            <thead>
            <tr>
              <th>FCAR ID</th>
              <th>Instructor</th>
              <th>Semester</th>
              <th>Year</th>
              <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${entry.value.fcarList}" var="fcar">
              <tr>
                <td>${fcar.fcarId}</td>
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
  </c:forEach>
</div>

<jsp:include page="footer.jsp" />

<script>
  // Course selection change handler
  document.getElementById('courseSelect').addEventListener('change', function() {
    const selectedCourse = this.value;

    // Hide all course sections
    document.querySelectorAll('.course-section').forEach(section => {
      section.style.display = 'none';
    });

    // Show selected section
    if (selectedCourse === '') {
      document.getElementById('overviewSection').style.display = 'block';
    } else {
      document.getElementById('course' + selectedCourse).style.display = 'block';
    }
  });

  // Combined Course Chart
  const combinedCtx = document.getElementById('combinedCourseChart').getContext('2d');
  const courseLabels = [];
  const performanceData = [];

  <c:forEach items="${courseReports}" var="entry">
  <c:set var="avgPerformance" value="0" />
  <c:set var="count" value="0" />

  <c:forEach items="${allCoursePerformance[entry.key]}" var="perf">
  <c:set var="avgPerformance" value="${avgPerformance + perf.value}" />
  <c:set var="count" value="${count + 1}" />
  </c:forEach>

  <c:if test="${count > 0}">
  courseLabels.push("${entry.key}");
  performanceData.push(${avgPerformance / count});
  </c:if>
  </c:forEach>

  new Chart(combinedCtx, {
    type: 'bar',
    data: {
      labels: courseLabels,
      datasets: [{
        label: 'Course Overall Performance',
        data: performanceData,
        backgroundColor: 'rgba(153, 102, 255, 0.5)',
        borderColor: 'rgba(153, 102, 255, 1)',
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
            text: 'Performance (%)'
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

  // Individual Course Charts
  <c:forEach items="${courseReports}" var="entry">
  const ctx${entry.key} = document.getElementById('chart${entry.key}').getContext('2d');
  const labels${entry.key} = [];
  const data${entry.key} = [];

  <c:forEach items="${allCoursePerformance[entry.key]}" var="perf">
  labels${entry.key}.push("${perf.key}");
  data${entry.key}.push(${perf.value});
  </c:forEach>

  new Chart(ctx${entry.key}, {
    type: 'bar',
    data: {
      labels: labels${entry.key},
      datasets: [{
        label: 'Performance Indicators for ${entry.key}',
        data: data${entry.key},
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
            text: 'Performance Indicator'
          }
        }
      }
    }
  });
  </c:forEach>

  // Show overview section by default
  document.getElementById('overviewSection').style.display = 'block';
</script>
</body>
</html>