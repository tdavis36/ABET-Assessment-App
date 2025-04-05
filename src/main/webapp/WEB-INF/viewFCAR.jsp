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
                            <button class="btn toggle-details" onclick="toggleDetails('fcar-${fcar.fcarId}')">Show Details</button>
                        </div>

                        <div id="fcar-${fcar.fcarId}" class="fcar-details" style="display: none;">
                            <!-- Actions Section - Moved to top for better visibility -->
                            <div class="fcar-section">
                                <div class="fcar-actions" style="display: flex; justify-content: flex-end; gap: 10px; margin-bottom: 15px; border: none; padding-top: 0;">
                                    <form method="get" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                        <input type="hidden" name="action" value="editFCAR"/>
                                        <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                        <button type="submit" class="btn">Edit FCAR</button>
                                    </form>

                                    <c:if test="${fcar.status == 'Submitted'}">
                                        <form method="post" action="${pageContext.request.contextPath}/AdminServlet" style="display:inline;">
                                            <input type="hidden" name="action" value="approveFCAR"/>
                                            <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                            <button type="submit" class="btn" style="background-color: #28a745;">Approve</button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/AdminServlet" style="display:inline;">
                                            <input type="hidden" name="action" value="rejectFCAR"/>
                                            <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                            <button type="submit" class="btn" style="background-color: #dc3545;">Reject</button>
                                        </form>
                                    </c:if>

                                    <c:if test="${fcar.status == 'Draft'}">
                                        <form method="post" action="${pageContext.request.contextPath}/ProfessorServlet" style="display:inline;">
                                            <input type="hidden" name="action" value="submitFCARStatus"/>
                                            <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                            <button type="submit" class="btn" style="background-color: #17a2b8;">Submit FCAR</button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                            <!-- Outcomes and Indicators Section -->
                            <c:if test="${not empty fcar.assessmentMethods['selectedOutcomes']}">
                                <div class="fcar-section">
                                    <h3>Outcomes and Indicators</h3>
                                    <p><strong>Target Goal:</strong> ${fcar.assessmentMethods['targetGoal']}%</p>

                                    <c:forEach var="outcomeId" items="${fcar.assessmentMethods['selectedOutcomes'].split(',')}" varStatus="status">
                                        <div style="margin-top: 15px; padding: 10px; background-color: #f5f5f5; border-radius: 5px;">
                                            <div style="font-weight: bold; margin-bottom: 10px;">
                                                <c:choose>
                                                    <c:when test="${outcomeId == '1'}">
                                                        <strong>Outcome 1:</strong> Analyze a complex computing problem and to apply principles of computing and other relevant disciplines to identify solutions.
                                                    </c:when>
                                                    <c:when test="${outcomeId == '2'}">
                                                        <strong>Outcome 2:</strong> Design, implement, and evaluate a computing-based solution to meet a given set of computing requirements in the context of the program's discipline.
                                                    </c:when>
                                                    <c:when test="${outcomeId == '3'}">
                                                        <strong>Outcome 3:</strong> Communicate effectively in a variety of professional contexts.
                                                    </c:when>
                                                    <c:when test="${outcomeId == '4'}">
                                                        <strong>Outcome 4:</strong> Recognize professional responsibilities and make informed judgments in computing practice based on legal and ethical principles.
                                                    </c:when>
                                                    <c:when test="${outcomeId == '5'}">
                                                        <strong>Outcome 5:</strong> Function effectively as a member or leader of a team engaged in activities appropriate to the program's discipline.
                                                    </c:when>
                                                    <c:when test="${outcomeId == '6'}">
                                                        <strong>Outcome 6:</strong> Apply computer science theory and software development fundamentals to produce computing-based solutions.
                                                    </c:when>
                                                </c:choose>
                                            </div>

                                            <!-- Display indicators for this outcome -->
                                            <div style="margin-left: 20px;">
                                                <strong>Indicators:</strong>
                                                <ul>
                                                    <c:forEach var="entry" items="${fcar.assessmentMethods}">
                                                        <c:if test="${entry.key.startsWith('indicator_') && entry.value == 'selected' && entry.key.contains(outcomeId)}">
                                                            <c:set var="indicatorValue" value="${entry.key.substring(entry.key.indexOf('_') + 1)}" />
                                                            <li>
                                                                <c:choose>
                                                                    <c:when test="${indicatorValue == '1.1'}">
                                                                        1.1 Student can correctly interpret a computational problem and define its parameters
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '1.2'}">
                                                                        1.2 Student can analyze a computational problem in order to choose mathematical and algorithmic principles that can be applied to solve the problem
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '1.3'}">
                                                                        1.3 Student can define a solution to a computational problem
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '1.4'}">
                                                                        1.4 Student can effectively collect and document system requirements
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '1.5'}">
                                                                        1.5 Student can effectively analyze and model a problem domain
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '1.6'}">
                                                                        1.6 Student can identify the relative efficiency of different algorithms using asymptotic notation
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '2.1'}">
                                                                        2.1 Student can identify and evaluate appropriate technologies to be used in a system
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '2.2'}">
                                                                        2.2 Student can effectively construct a design model of a system
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '2.3'}">
                                                                        2.3 Student can effectively incorporate requirements outside the problem domain (e.g., a user interface) into the design model
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '2.4'}">
                                                                        2.4 Student can plan and implement a testing strategy to ensure that system meets its quality goal
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '2.5'}">
                                                                        2.5 Student can collect and analyze runtime benchmark data to characterize the efficiency of an algorithm or data structure
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '2.6'}">
                                                                        2.6 Student can specify appropriate security concerns and requirements for a component or system
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '2.7'}">
                                                                        2.7 Student can evaluate a component or system to identify security characteristics and identify vulnerabilities
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '3.1'}">
                                                                        3.1 Student can write a clear and well-organized technical report
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '3.2'}">
                                                                        3.2 Student can create and present a clear and well-organized technical presentation using appropriate visual, textual, and spoken content
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '3.3'}">
                                                                        3.3 Student can communicate technical content to peers
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '3.4'}">
                                                                        3.4 Student can communicate technical content to general audiences
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '4.1'}">
                                                                        4.1 Student can analyze and explain the ethical issues surrounding a particular computing topic (for example, peer-to-peer file sharing)
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '4.2'}">
                                                                        4.2 Student demonstrates recognition of his or her professional responsibilities as a member of the computing profession
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '5.1'}">
                                                                        5.1 Student demonstrates an ability to participate in and implement processes for team communication and coordination
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '5.2'}">
                                                                        5.2 Student demonstrates an ability to work closely with other students to solve technical problems
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '6.1'}">
                                                                        6.1 Student is proficient in a current programming language
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '6.2'}">
                                                                        6.2 Student can create user interfaces using current platforms
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '6.3'}">
                                                                        6.3 Student can write programs that use concurrency
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '6.4'}">
                                                                        6.4 Student can implement automated tests to satisfy the goals of a testing strategy
                                                                    </c:when>
                                                                    <c:when test="${indicatorValue == '6.5'}">
                                                                        6.5 Student can use appropriate implementation techniques and practices to meet security requirements and/or mitigate discovered vulnerabilities
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        ${indicatorValue}
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </li>
                                                        </c:if>
                                                    </c:forEach>
                                                </ul>
                                            </div>
                                        </div>
                                    </c:forEach>
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
                            <c:if test="${not empty fcar.assessmentMethods['level3']}">
                                <div class="fcar-section">
                                    <h3>Achievement Levels</h3>
                                    <table class="achievement-table">
                                        <thead>
                                        <tr>
                                            <th>Exceeds Expectations</th>
                                            <th>Meets Expectations</th>
                                            <th>Below Expectations</th>
                                            <th>Total Students</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td>${fcar.assessmentMethods['level3']}</td>
                                            <td>${fcar.assessmentMethods['level2']}</td>
                                            <td>${fcar.assessmentMethods['level1']}</td>
                                            <td>${fcar.assessmentMethods['totalStudents']}</td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </c:if>

                            <!-- Results Section -->
                            <c:if test="${not empty fcar.assessmentMethods['totalStudents']}">
                                <div class="fcar-section">
                                    <h3>Results</h3>
                                    <p><strong>Students Meeting Target (Level 3):</strong> ${fcar.assessmentMethods['studentsMetTarget']}</p>
                                    <p><strong>Percentage Meeting Target:</strong> ${fcar.assessmentMethods['percentageMetTarget']}%</p>
                                    <p><strong>Target Goal Met:</strong>
                                        <span style="font-weight: bold; color: ${fcar.assessmentMethods['targetMet'] == 'true' ? 'green' : 'red'}">
                                                ${fcar.assessmentMethods['targetMet'] == 'true' ? 'Yes' : 'No'}
                                        </span>
                                    </p>
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