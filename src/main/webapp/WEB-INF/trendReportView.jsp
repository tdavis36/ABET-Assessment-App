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
    <p>Date Range: ${startYear} to ${endYear}</p>
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
      <div class="card-header">Trend Analysis Summary</div>
      <div class="card-body">
        <p>This report shows ABET outcome achievement trends from ${startYear} to ${endYear}.</p>
        <p>Total FCARs included: ${report.fcarList.size()}</p>
      </div>
    </div>
  </div>

  <!-- Trend Chart -->
  <div class="row">
    <div class="col-md-12">
      <div class="card">
        <div class="card-header">Outcome Achievement Trends (${startYear}-${endYear})</div>
        <div class="card-body">
          <canvas id="trendChart"></canvas>
        </div>
      </div>
    </div>
  </div>

  <!-- Yearly Data Tables -->
  <div class="row mt-4">
    <div class="col-md-12">
      <div class="card">
        <div class="card-header">Yearly Outcome Achievement Data</div>
        <div class="card-body">
          <table class="table table-striped">
            <thead>
            <tr>
              <th>Year</th>
              <c:forEach items="${yearlyOutcomeStatistics[startYear]}" var="outcome">
                <th>Outcome ${outcome.key}</th>
              </c:forEach>
              <th>Overall</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="year" begin="${startYear}" end="${endYear}">
              <tr>
                <td>${year}</td>
                <c:set var="yearTotal" value="0" />
                <c:set var="outcomeCount" value="0" />

                <c:forEach items="${yearlyOutcomeStatistics[year]}" var="outcome">
                  <td><fmt:formatNumber value="${outcome.value}" maxFractionDigits="1" />%</td>
                  <c:set var="yearTotal" value="${yearTotal + outcome.value}" />
                  <c:set var="outcomeCount" value="${outcomeCount + 1}" />
                </c:forEach>

                <td>
                  <c:if test="${outcomeCount > 0}">
                    <strong>
                      <fmt:formatNumber value="${yearTotal / outcomeCount}" maxFractionDigits="1" />%
                    </strong>
                  </c:if>
                  <c:if test="${outcomeCount == 0}">
                    N/A
                  </c:if>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <!-- FCARs Table -->
  <div class="row mt-4">
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
  // Trend Chart
  const trendCtx = document.getElementById('trendChart').getContext('2d');

  // Prepare datasets for each outcome
  const outcomes = [];
  const datasets = [];
  const years = [];

  // Add years to array
  <c:forEach var="year" begin="${startYear}" end="${endYear}">
  years.push(${year});
  </c:forEach>

  // Get all unique outcome IDs
  <c:forEach items="${yearlyOutcomeStatistics[startYear]}" var="outcome">
  outcomes.push("${outcome.key}");
  </c:forEach>

  // Create a dataset for each outcome
  outcomes.forEach((outcome, index) => {
    const data = [];
    years.forEach(year => {
      <c:forEach var="year" begin="${startYear}" end="${endYear}">
      if (year === ${year}) {
        <c:forEach items="${yearlyOutcomeStatistics[year]}" var="stat">
        if (outcome === "${stat.key}") {
          data.push(${stat.value});
        }
        </c:forEach>
      }
      </c:forEach>
    });

    // Generate a color based on the index
    const hue = (index * 137) % 360;
    const color = `hsl(${hue}, 70%, 60%)`;

    datasets.push({
      label: `Outcome ${outcome}`,
      data: data,
      backgroundColor: color,
      borderColor: color,
      borderWidth: 2,
      fill: false,
      tension: 0.1
    });
  });

  // Create the line chart
  new Chart(trendCtx, {
    type: 'line',
    data: {
      labels: years,
      datasets: datasets
    },
    options: {
      responsive: true,
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
            text: 'Year'
          }
        }
      }
    }
  });
</script>
</body>
</html>