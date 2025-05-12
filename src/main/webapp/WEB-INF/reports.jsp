<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>FCAR-Based Reports Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="dashboard" id="fcarReportsDashboard">
    <div class="header-container">
        <h1>FCAR Reports Dashboard</h1>
        <p>Select filters below to generate FCAR reports based on course assignments, outcomes, indicators, and more.</p>
    </div>

    <!-- Filter & Generate Section -->
    <div class="section generate-report">
        <h2>Generate FCAR Report</h2>
        <form action="${pageContext.request.contextPath}/reports" method="get" class="form-inline">
            <!-- Action for servlet to dispatch -->
            <input type="hidden" name="action" value="generateFCARReport" />

            <div class="form-group">
                <label for="courseCode">Course:</label>
                <select id="courseCode" name="courseCode" class="form-control">
                    <option value="">-- All Courses --</option>
                    <c:forEach var="c" items="${courses}">
                        <option value="${c.courseCode}">${c.courseCode} - ${c.courseName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="professorId">Professor:</label>
                <select id="professorId" name="professorId" class="form-control">
                    <option value="">-- All Professors --</option>
                    <c:forEach var="p" items="${professors}">
                        <option value="${p.userId}">${p.firstName} ${p.lastName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="semester">Semester:</label>
                <select id="semester" name="semester" class="form-control">
                    <option value="">-- All Semesters --</option>
                    <c:forEach var="sem" items="${semesters}">
                        <option value="${sem}">${sem}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="year">Year:</label>
                <select id="year" name="year" class="form-control">
                    <option value="">-- All Years --</option>
                    <c:forEach var="yr" items="${years}">
                        <option value="${yr}">${yr}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="outcomeId">Outcome:</label>
                <select id="outcomeId" name="outcomeId" class="form-control" onchange="onOutcomeChange()">
                    <option value="">-- All Outcomes --</option>
                    <c:forEach var="o" items="${outcomes}">
                        <option value="${o.outcomeId}">${o.outcomeId} - ${o.name}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="indicatorId">Indicator:</label>
                <select id="indicatorId" name="indicatorId" class="form-control">
                    <option value="">-- All Indicators --</option>
                    <!-- Options populated dynamically based on selected outcome -->
                </select>
            </div>

            <div class="form-group">
                <label for="status">FCAR Status:</label>
                <select id="status" name="status" class="form-control">
                    <option value="">-- Any Status --</option>
                    <c:forEach var="st" items="${statuses}">
                        <option value="${st}">${st}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="viewType">View:</label>
                <select id="viewType" name="viewType" class="form-control">
                    <option value="summary">Summary Report</option>
                    <option value="detail">Detailed Report</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary">Generate</button>
        </form>
    </div>

    <!-- Previously Generated FCAR Reports -->
    <div class="section available-reports">
        <h2>Available FCAR Reports</h2>
        <c:choose>
            <c:when test="${not empty generatedReports}">
                <div class="report-list">
                    <c:forEach var="r" items="${generatedReports}">
                        <div class="report-item">
                            <span class="report-title">${r.title}</span>
                            <span class="report-date">${r.generatedAt}</span>
                            <div class="report-actions">
                                <form action="${pageContext.request.contextPath}/reports" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="downloadFCARReport" />
                                    <input type="hidden" name="reportId" value="${r.id}" />
                                    <button type="submit" class="btn btn-sm">Download (.xlsx)</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/reports" method="get" style="display:inline;">
                                    <input type="hidden" name="action" value="viewFCARReport" />
                                    <input type="hidden" name="reportId" value="${r.id}" />
                                    <button type="submit" class="btn btn-sm">View</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p>No FCAR reports generated yet. Use the form above to create one.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/footer.jsp" />

<script>
    // Prepare JS map for indicators by outcome
    const indicatorsByOutcome = {
        <c:forEach var="entry" items="${indicatorsByOutcome.entrySet()}" varStatus="loop">
        "${entry.key}": [
            <c:forEach var="ind" items="${entry.value}" varStatus="innerLoop">
            { id: "${ind.indicatorId}", name: "${ind.description}" }<c:if test="${!innerLoop.last}">,</c:if>
            </c:forEach>
        ]<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    };

    function onOutcomeChange() {
        const outcomeSelect = document.getElementById('outcomeId');
        const indicatorSelect = document.getElementById('indicatorId');
        const selected = outcomeSelect.value;
        // Clear existing options
        indicatorSelect.innerHTML = '<option value="">-- All Indicators --</option>';
        if (selected && indicatorsByOutcome[selected]) {
            indicatorsByOutcome[selected].forEach(ind => {
                const opt = document.createElement('option');
                opt.value = ind.id;
                opt.text = ind.name;
                indicatorSelect.appendChild(opt);
            });
        }
    }
</script>
</body>
</html>
