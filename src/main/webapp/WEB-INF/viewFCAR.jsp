<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.ABETAppTeam.FCAR" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>View FCARs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .fcar-list {
            list-style-type: none;
            padding: 0;
        }
        .fcar-item {
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 20px;
            padding: 15px;
            background-color: #f9f9f9;
        }
        .fcar-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            padding-bottom: 10px;
            border-bottom: 1px solid #ddd;
        }
        .fcar-details {
            margin-top: 15px;
        }
        .fcar-section {
            margin-bottom: 15px;
        }
        .fcar-section h3 {
            margin-bottom: 5px;
            color: #333;
        }
        .status-badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 0.8em;
            font-weight: bold;
        }
        .status-draft { background-color: #f0f0f0; color: #666; }
        .status-submitted { background-color: #cce5ff; color: #004085; }
        .status-approved { background-color: #d4edda; color: #155724; }
        .status-rejected { background-color: #f8d7da; color: #721c24; }
        .achievement-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        .achievement-table th, .achievement-table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
        }
        .achievement-table th {
            background-color: #f2f2f2;
        }
        .toggle-details {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 5px 10px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            margin: 4px 2px;
            cursor: pointer;
            border-radius: 4px;
        }
    </style>
</head>
<body>
<h1>Faculty Course Assessment Reports (FCARs)</h1>
<div style="display: flex; gap: 10px; margin-bottom: 20px;">
    <a href="${pageContext.request.contextPath}/ProfessorServlet" class="btn">Back to Professor Dashboard</a>
    <a href="${pageContext.request.contextPath}/AdminServlet" class="btn">Back to Admin Dashboard</a>
</div>

<div class="section">
    <h2>Existing FCARs</h2>
    <c:choose>
        <c:when test="${not empty allFCARs}">
            <ul class="fcar-list">
                <c:forEach var="fcar" items="${allFCARs}" varStatus="status">
                    <li class="fcar-item">
                        <div class="fcar-header">
                            <div>
                                <h3>FCAR #${status.index + 1}: ${fcar.courseId}</h3>
                                <div>
                                    <strong>Professor:</strong> ${fcar.professorId} |
                                    <strong>Semester:</strong> ${fcar.semester} ${fcar.year} |
                                    <strong>Status:</strong> 
                                    <span class="status-badge status-${fcar.status.toLowerCase()}">${fcar.status}</span>
                                </div>
                            </div>
                            <button class="toggle-details" onclick="toggleDetails('fcar-${status.index}')">Show Details</button>
                        </div>
                        
                        <div id="fcar-${status.index}" class="fcar-details" style="display: none;">
                            <!-- Outcome and Indicator Section -->
                            <c:if test="${not empty fcar.assessmentMethods['outcome']}">
                                <div class="fcar-section">
                                    <h3>Outcome and Indicator</h3>
                                    <p><strong>Outcome:</strong> ${fcar.assessmentMethods['outcome']}</p>
                                    <p><strong>Indicator:</strong> ${fcar.assessmentMethods['indicator']}</p>
                                    <p><strong>Target Goal:</strong> ${fcar.assessmentMethods['targetGoal']}%</p>
                                </div>
                            </c:if>
                            
                            <!-- Assessment Method Section -->
                            <c:if test="${not empty fcar.assessmentMethods['workUsed']}">
                                <div class="fcar-section">
                                    <h3>Assessment Method</h3>
                                    <p><strong>Work Used:</strong> ${fcar.assessmentMethods['workUsed']}</p>
                                    <p><strong>Description:</strong> ${fcar.assessmentMethods['assessmentDescription']}</p>
                                </div>
                            </c:if>
                            
                            <!-- Achievement Levels Section -->
                            <c:if test="${not empty fcar.assessmentMethods['level4']}">
                                <div class="fcar-section">
                                    <h3>Achievement Levels</h3>
                                    <table class="achievement-table">
                                        <thead>
                                            <tr>
                                                <th>Exemplary (4)</th>
                                                <th>Satisfactory (3)</th>
                                                <th>Developing (2)</th>
                                                <th>Unsatisfactory (1)</th>
                                                <th>Not Applicable (0)</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>${fcar.assessmentMethods['level4']}</td>
                                                <td>${fcar.assessmentMethods['level3']}</td>
                                                <td>${fcar.assessmentMethods['level2']}</td>
                                                <td>${fcar.assessmentMethods['level1']}</td>
                                                <td>${fcar.assessmentMethods['level0']}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </c:if>
                            
                            <!-- Results Section -->
                            <c:if test="${not empty fcar.assessmentMethods['totalStudents']}">
                                <div class="fcar-section">
                                    <h3>Results</h3>
                                    <p><strong>Total Students:</strong> ${fcar.assessmentMethods['totalStudents']}</p>
                                    <p><strong>Students Meeting Target (Level 3 or 4):</strong> ${fcar.assessmentMethods['studentsMetTarget']}</p>
                                    <p><strong>Percentage Meeting Target:</strong> ${fcar.assessmentMethods['percentageMetTarget']}%</p>
                                    <p><strong>Target Goal Met:</strong> ${fcar.assessmentMethods['targetMet']}</p>
                                </div>
                            </c:if>
                            
                            <!-- Summary and Improvement Actions Section -->
                            <c:if test="${not empty fcar.improvementActions['summary']}">
                                <div class="fcar-section">
                                    <h3>Summary and Improvement Actions</h3>
                                    <p><strong>Summary:</strong> ${fcar.improvementActions['summary']}</p>
                                    <c:if test="${not empty fcar.improvementActions['actions']}">
                                        <p><strong>Improvement Actions:</strong> ${fcar.improvementActions['actions']}</p>
                                    </c:if>
                                </div>
                            </c:if>
                            
                            <!-- Major Breakdown Section -->
                            <c:set var="hasMajors" value="false" />
                            <c:forEach var="method" items="${fcar.assessmentMethods}">
                                <c:if test="${method.key.startsWith('major_')}">
                                    <c:set var="hasMajors" value="true" />
                                </c:if>
                            </c:forEach>
                            
                            <c:if test="${hasMajors}">
                                <div class="fcar-section">
                                    <h3>Achievement Levels by Major</h3>
                                    <c:set var="majorIndex" value="0" />
                                    <c:set var="majorPrefix" value="major_${majorIndex}_" />
                                    
                                    <c:forEach begin="0" end="10" varStatus="loop">
                                        <c:set var="currentPrefix" value="major_${loop.index}_" />
                                        <c:if test="${not empty fcar.assessmentMethods[currentPrefix.concat('name')]}">
                                            <div style="margin-top: 15px;">
                                                <h4>${fcar.assessmentMethods[currentPrefix.concat('name')]}</h4>
                                                <table class="achievement-table">
                                                    <thead>
                                                        <tr>
                                                            <th>Exemplary (4)</th>
                                                            <th>Satisfactory (3)</th>
                                                            <th>Developing (2)</th>
                                                            <th>Unsatisfactory (1)</th>
                                                            <th>Not Applicable (0)</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <tr>
                                                            <td>${fcar.assessmentMethods[currentPrefix.concat('level4')]}</td>
                                                            <td>${fcar.assessmentMethods[currentPrefix.concat('level3')]}</td>
                                                            <td>${fcar.assessmentMethods[currentPrefix.concat('level2')]}</td>
                                                            <td>${fcar.assessmentMethods[currentPrefix.concat('level1')]}</td>
                                                            <td>${fcar.assessmentMethods[currentPrefix.concat('level0')]}</td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:when>
        <c:otherwise>
            <p>No FCARs available.</p>
        </c:otherwise>
    </c:choose>
</div>

<script>
    function toggleDetails(id) {
        const details = document.getElementById(id);
        const button = event.target;
        
        if (details.style.display === "none") {
            details.style.display = "block";
            button.textContent = "Hide Details";
        } else {
            details.style.display = "none";
            button.textContent = "Show Details";
        }
    }
</script>
</body>
</html>
