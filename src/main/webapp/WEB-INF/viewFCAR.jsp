<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.ABETAppTeam.model.FCAR" %>
<%@ page import="com.ABETAppTeam.repository.FCARRepository" %>
<%@ page import="com.ABETAppTeam.repository.OutcomeRepository" %>
<%@ page import="com.ABETAppTeam.repository.IndicatorRepository" %>
<%@ page import="com.ABETAppTeam.model.Outcome" %>
<%@ page import="com.ABETAppTeam.model.Indicator" %>
<%@ page import="com.ABETAppTeam.model.User" %>
<%@ page import="com.ABETAppTeam.model.Admin" %>
<%@ page import="com.ABETAppTeam.model.Professor" %>
<%@ page import="com.ABETAppTeam.controller.DisplaySystemController" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>FCAR View</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />
<div class="dashboard" id="fcarViewDashboard">
    <div class="header-container">
        <h1>FCAR Details</h1>
    </div>

    <!-- Message Display Section -->
    <c:if test="${(successMessage != null && not empty successMessage) || (errorMessage != null && not empty errorMessage)}">
        <div class="message-container">
            <c:if test="${successMessage != null && not empty successMessage}">
                <div class="success-message">
                    <p>${successMessage}</p>
                </div>
            </c:if>
            <c:if test="${errorMessage != null && not empty errorMessage}">
                <div class="error-message">
                    <p>${errorMessage}</p>
                </div>
            </c:if>
        </div>
    </c:if>

    <!-- Status Key -->
    <div class="status-key">
        <div><span class="status draft"></span> Draft</div>
        <div><span class="status submitted"></span> Submitted</div>
        <div><span class="status approved"></span> Approved</div>
        <div><span class="status rejected"></span> Rejected</div>
    </div>

    <div class="section">
        <h2>Existing FCARs</h2>
        <%
            User user = (User) session.getAttribute("user");
            String dashboardUrl;
            if (user instanceof Admin) request.getContextPath();
            else if (user instanceof Professor) {
            }

            // Initialize repositories for outcomes and indicators
            OutcomeRepository outcomeRepository = new OutcomeRepository();
            IndicatorRepository indicatorRepository = new IndicatorRepository();

            // Create a map to store outcome information
            Map<Integer, Outcome> outcomeMap = new HashMap<>();
            for (Outcome outcome : outcomeRepository.findAll()) {
                outcomeMap.put(outcome.getId(), outcome);
            }

            // Create a map to store indicator information
            Map<Integer, List<Indicator>> indicatorMap = new HashMap<>();
            for (Indicator indicator : indicatorRepository.findAll()) {
                int outcomeId = indicator.getOutcomeId();
                if (!indicatorMap.containsKey(outcomeId)) {
                    indicatorMap.put(outcomeId, new ArrayList<>());
                }
                indicatorMap.get(outcomeId).add(indicator);
            }

            // Set attributes for JSTL access
            request.setAttribute("outcomeMap", outcomeMap);
            request.setAttribute("indicatorMap", indicatorMap);

            // Check if we're viewing a specific FCAR
            FCAR selectedFCAR = (FCAR) request.getAttribute("selectedFCAR");

            // If we're not viewing a specific FCAR and allFCARs is not set, fetch all FCARs
            if (selectedFCAR == null && request.getAttribute("allFCARs") == null) {
                FCARRepository repo = new FCARRepository();
                List<FCAR> allFCARs = (user instanceof Admin)
                        ? repo.findAll()
                        : repo.findByInstructorId(user.getUserId());
                request.setAttribute("allFCARs", allFCARs);
            }

            // If we're viewing a specific FCAR, set the fcarId parameter for the Edit button
            if (selectedFCAR != null) {
                request.setAttribute("fcarId", selectedFCAR.getFcarId());
            }
        %>
        <c:choose>
            <c:when test="${not empty selectedFCAR}">
                <!-- Display a single FCAR when selectedFCAR is set -->
                <ul class="fcar-list">
                    <li class="fcar-item">
                        <div class="fcar-header">
                            <div>
                                <h3>FCAR: ${selectedFCAR.courseCode} <c:if test="${course != null && not empty course}">- ${course.courseName}</c:if></h3>
                                <p>
                                    <strong>Professor:</strong>
                                    <c:choose>
                                        <c:when test="${professor != null && not empty professor}">
                                            ${professor.firstName} ${professor.lastName}
                                        </c:when>
                                        <c:otherwise>
                                            ID: ${selectedFCAR.instructorId}
                                        </c:otherwise>
                                    </c:choose> |
                                    <strong>Semester:</strong> ${selectedFCAR.semester} ${selectedFCAR.year} |
                                    <strong>Status:</strong>
                                    <span class="status-badge status-${fn:toLowerCase(selectedFCAR.status)}">${selectedFCAR.status}</span>
                                </p>
                            </div>
                            <div>
                                <button class="btn toggle-details" onclick="toggleDetails('fcar-${selectedFCAR.fcarId}')">Show Details</button>
                                <c:if test="${user.userId == selectedFCAR.instructorId || user.roleId == 1}">
                                    <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${selectedFCAR.fcarId}" class="btn">Edit FCAR</a>
                                </c:if>
                            </div>
                        </div>
                        <div id="fcar-${selectedFCAR.fcarId}" class="fcar-details" style="display:none;">
                            <div class="fcar-section">
                                <h4>Actions</h4>
                                <c:if test="${user.userId == selectedFCAR.instructorId || user.roleId == 1}">
                                    <p><strong>Edit Status:</strong>
                                        <c:choose>
                                            <c:when test="${selectedFCAR.status == 'Draft' || user.roleId == 1}">Available</c:when>
                                            <c:otherwise>Not available for ${selectedFCAR.status} FCARs</c:otherwise>
                                        </c:choose>
                                    </p>
                                </c:if>
                                <c:if test="${user.roleId == 1 && selectedFCAR.status == 'Submitted'}">
                                    <p><strong>Admin Actions:</strong>
                                        <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${selectedFCAR.fcarId}" class="btn">Edit FCAR</a>
                                        Approval pending
                                    </p>
                                </c:if>
                            </div>
                            <c:if test="${selectedFCAR != null && selectedFCAR.outcomeId > 0}">
                                <div class="fcar-section">
                                    <h4>Student Learning Outcomes</h4>
                                    <c:set var="outcome" value="${outcomeMap[selectedFCAR.outcomeId]}" />
                                    <p><strong>Outcome:</strong>
                                        <c:choose>
                                            <c:when test="${outcome != null}">
                                                ${outcome.outcomeNum}: ${outcome.description}
                                            </c:when>
                                            <c:otherwise>
                                                Outcome ID: ${selectedFCAR.outcomeId}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>

                                    <c:set var="indicators" value="${indicatorMap[selectedFCAR.outcomeId]}" />
                                    <c:set var="indicatorFound" value="false" />
                                    <c:if test="${indicators != null}">
                                        <c:forEach var="indicator" items="${indicators}">
                                            <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                                <c:set var="indicatorFound" value="true" />
                                            </c:if>
                                        </c:forEach>
                                    </c:if>

                                    <c:if test="${indicators == null || empty indicators || indicatorFound == false}">
                                        <p><strong>Indicator ID:</strong> ${selectedFCAR.indicatorId}</p>
                                    </c:if>
                                    <c:if test="${indicators != null}">
                                        <c:forEach var="indicator" items="${indicators}">
                                            <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                                <p><strong>Indicator:</strong> ${selectedFCAR.outcomeId}.${indicator.number}: ${indicator.description}</p>
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                </div>
                            </c:if>
                            <c:if test="${selectedFCAR.outcomeId != null && not empty selectedFCAR.outcomeId}">
                                <div class="fcar-section">
                                    <h4>Student Learning Outcomes</h4>
                                    <c:set var="outcome" value="${outcomeMap[selectedFCAR.outcomeId]}" />
                                    <p><strong>Outcome:</strong>
                                        <c:choose>
                                            <c:when test="${outcome != null && not empty outcome}">
                                                ${outcome.outcomeNum}: ${outcome.description}
                                            </c:when>
                                            <c:otherwise>
                                                Outcome ID: ${selectedFCAR.outcomeId}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>

                                    <c:set var="indicators" value="${indicatorMap[selectedFCAR.outcomeId]}" />
                                    <c:set var="indicatorFound" value="false" />
                                    <c:forEach var="indicator" items="${indicators}">
                                        <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                           <c:set var="indicatorFound" value="true" />
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${indicators == null || empty indicators || not indicatorFound}">
                                        <p><strong>Indicator ID:</strong> ${selectedFCAR.indicatorId}</p>
                                    </c:if>
                                    <c:forEach var="indicator" items="${indicators}">
                                        <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                            <p><strong>Indicator:</strong> ${selectedFCAR.outcomeId}.${indicator.number}: ${indicator.description}</p>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </c:if>
                            <c:if test="${(assessmentMethods != null && not empty assessmentMethods) || (selectedFCAR != null && selectedFCAR.assessmentMethods != null && not empty selectedFCAR.assessmentMethods)}">
                                <div class="fcar-section">
                                    <h4>Assessment Methods</h4>

                                        <%-- Use appropriate map based on what's available --%>
                                    <c:set var="methodsMap" value="${assessmentMethods != null && not empty assessmentMethods ? assessmentMethods : selectedFCAR.assessmentMethods}" />

                                        <%-- Display Assessment Description and Work Used in a structured way --%>
                                    <div class="assessment-details">
                                        <c:if test="${methodsMap['assessmentDescription'] != null}">
                                            <div class="form-group">
                                                <strong>Assessment Description:</strong>
                                                <div class="content-box">${methodsMap['assessmentDescription']}</div>
                                            </div>
                                        </c:if>

                                        <c:if test="${methodsMap['workUsed'] != null}">
                                            <div class="form-group">
                                                <strong>Work Used:</strong>
                                                <div class="content-box">${methodsMap['workUsed']}</div>
                                            </div>
                                        </c:if>

                                        <c:if test="${methodsMap['targetGoal'] != null}">
                                            <div class="form-group">
                                                <strong>Target Goal:</strong>
                                                <div class="content-box">${methodsMap['targetGoal']}%</div>
                                            </div>
                                        </c:if>
                                    </div>

                                        <%-- Display selected outcomes and indicators if available --%>
                                    <c:if test="${methodsMap['selectedOutcomes'] != null}">
                                        <div class="form-group">
                                            <strong>Selected Outcomes and Indicators:</strong>
                                            <div class="content-box">
                                                <c:set var="outcomeIds" value="${fn:split(methodsMap['selectedOutcomes'], ',')}" />
                                                <table class="data-table">
                                                    <thead>
                                                    <tr>
                                                        <th>Outcome</th>
                                                        <th>Indicators</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    <c:forEach var="outcomeId" items="${outcomeIds}">
                                                        <tr>
                                                            <td>
                                                                    <%-- Try to get outcome description from outcomeMap --%>
                                                                <c:choose>
                                                                    <c:when test="${outcomeMap != null && outcomeMap[outcomeId] != null}">
                                                                        Outcome ${outcomeId}: ${outcomeMap[outcomeId].description}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        Outcome ${outcomeId}
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                    <%-- Look for indicators related to this outcome --%>
                                                                <c:set var="hasIndicators" value="false" />
                                                                <c:forEach var="entry" items="${methodsMap}">
                                                                    <c:if test="${fn:startsWith(entry.key, 'indicator_'.concat(outcomeId))}">
                                                                        <c:set var="hasIndicators" value="true" />
                                                                        <div>
                                                                                <%-- Extract indicator number from the key --%>
                                                                            <c:set var="indicatorKey" value="${entry.key}" />
                                                                            <c:set var="indicatorNum" value="${fn:substringAfter(indicatorKey, 'indicator_'.concat(outcomeId).concat('.'))}" />
                                                                            <c:choose>
                                                                                <c:when test="${indicatorMap != null && indicatorMap[outcomeId] != null}">
                                                                                    <c:forEach var="indicator" items="${indicatorMap[outcomeId]}">
                                                                                        <c:if test="${indicator.number == indicatorNum}">
                                                                                            ${outcomeId}.${indicatorNum}: ${indicator.description}
                                                                                        </c:if>
                                                                                    </c:forEach>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    Indicator ${outcomeId}.${indicatorNum}
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </div>
                                                                    </c:if>
                                                                </c:forEach>
                                                                <c:if test="${!hasIndicators}">
                                                                    No specific indicators selected
                                                                </c:if>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </c:if>

                                        <%-- Display other assessment method entries that don't fit into the categories above --%>
                                    <c:set var="hasOtherEntries" value="false" />
                                    <c:forEach var="method" items="${methodsMap}">
                                        <c:if test="${!fn:startsWith(method.key, 'level') &&
                          method.key != 'assessmentDescription' &&
                          method.key != 'workUsed' &&
                          method.key != 'targetGoal' &&
                          method.key != 'selectedOutcomes' &&
                          !fn:startsWith(method.key, 'indicator_')}">
                                            <c:set var="hasOtherEntries" value="true" />
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${hasOtherEntries}">
                                        <div class="form-group">
                                            <strong>Other Assessment Details:</strong>
                                            <div class="content-box">
                                                <c:forEach var="method" items="${methodsMap}">
                                                    <c:if test="${!fn:startsWith(method.key, 'level') &&
                                      method.key != 'assessmentDescription' &&
                                      method.key != 'workUsed' &&
                                      method.key != 'targetGoal' &&
                                      method.key != 'selectedOutcomes' &&
                                      !fn:startsWith(method.key, 'indicator_')}">
                                                        <p><strong>${fn:replace(method.key, '_', ' ')}:</strong> ${method.value}</p>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </c:if>

                                        <%-- Now handle the level entries in a structured way --%>
                                    <c:if test="${methodsMap['level1'] != null || methodsMap['level2'] != null || methodsMap['level3'] != null}">
                                        <div class="form-group">
                                            <strong>Achievement Levels:</strong>
                                            <div class="content-box">
                                                <table class="achievement-table">
                                                    <thead>
                                                    <tr>
                                                        <th>Exceeds Expectations</th>
                                                        <th>Meets Expectations</th>
                                                        <th>Below Expectations</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    <tr>
                                                        <td>${methodsMap['level3'] != null ? methodsMap['level3'] : '0'}</td>
                                                        <td>${methodsMap['level2'] != null ? methodsMap['level2'] : '0'}</td>
                                                        <td>${methodsMap['level1'] != null ? methodsMap['level1'] : '0'}</td>
                                                    </tr>
                                                    </tbody>
                                                </table>

                                                    <%-- Calculate total and percentages --%>
                                                <c:set var="level1" value="${methodsMap['level1'] != null ? methodsMap['level1'] : 0}" />
                                                <c:set var="level2" value="${methodsMap['level2'] != null ? methodsMap['level2'] : 0}" />
                                                <c:set var="level3" value="${methodsMap['level3'] != null ? methodsMap['level3'] : 0}" />

                                                <c:set var="totalStudents" value="${level1 + level2 + level3}" />
                                                <c:set var="studentsMetTarget" value="${level2 + level3}" />
                                                <c:set var="targetGoal" value="${methodsMap['targetGoal'] != null ? methodsMap['targetGoal'] : 70}" />

                                                <c:if test="${totalStudents > 0}">
                                                    <div class="results-summary">
                                                        <p>Total Students: ${totalStudents}</p>
                                                        <p>Students Meeting or Exceeding Expectations: ${studentsMetTarget} (${Math.round(studentsMetTarget * 100 / totalStudents)}%)</p>
                                                        <p>Target Goal: ${targetGoal}% of students meet or exceed expectations</p>
                                                        <p>
                                                            <strong>Target Met: </strong>
                                                            <span class="${(studentsMetTarget * 100 / totalStudents) >= targetGoal ? 'status-met' : 'status-not-met'}">
                                                                    ${(studentsMetTarget * 100 / totalStudents) >= targetGoal ? 'Yes' : 'No'}
                                                            </span>
                                                        </p>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </c:if>
                            <c:if test="${(studentOutcomes != null && not empty studentOutcomes) || (selectedFCAR != null && selectedFCAR.studentOutcomes != null && not empty selectedFCAR.studentOutcomes)}">
                                <div class="fcar-section">
                                    <h4>Student Outcomes</h4>
                                    <c:choose>
                                        <c:when test="${studentOutcomes != null && not empty studentOutcomes}">
                                            <c:forEach var="outcome" items="${studentOutcomes}">
                                                <p><strong>${fn:replace(outcome.key, '_', ' ')}:</strong> ${outcome.value}</p>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="outcome" items="${selectedFCAR.studentOutcomes}">
                                                <p><strong>${fn:replace(outcome.key, '_', ' ')}:</strong> ${outcome.value}</p>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                            <c:if test="${(improvementActions != null && not empty improvementActions) || (selectedFCAR != null && selectedFCAR.improvementActions != null && not empty selectedFCAR.improvementActions)}">
                                <div class="fcar-section">
                                    <h4>Improvement Actions</h4>
                                    <c:choose>
                                        <c:when test="${improvementActions != null && not empty improvementActions}">
                                            <c:forEach var="act" items="${improvementActions}">
                                                <p><strong>${fn:replace(act.key, '_', ' ')}:</strong> ${act.value}</p>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="act" items="${selectedFCAR.improvementActions}">
                                                <p><strong>${fn:replace(act.key, '_', ' ')}:</strong> ${act.value}</p>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                        </div>
                    </li>
                </ul>
            </c:when>
            <c:when test="${not empty allFCARs}">
                <!-- Display all FCARs when allFCARs is set -->
                <ul class="fcar-list">
                    <c:forEach var="fcar" items="${allFCARs}" varStatus="status">
                        <li class="fcar-item">
                            <div class="fcar-header">
                                <div>
                                    <h3>FCAR #${status.index + 1}: ${fcar.courseCode}</h3>
                                    <p>
                                        <strong>Instructor:</strong>
                                        <%
                                            // Get the instructor for this FCAR
                                            FCAR currentFcar = (FCAR)pageContext.getAttribute("fcar");
                                            User instructor = DisplaySystemController.getInstance().getUser(currentFcar.getInstructorId());
                                            if (instructor != null) {
                                                out.print(instructor.getLastName());
                                            } else {
                                                out.print(currentFcar.getInstructorId());
                                            }
                                        %> |
                                        <strong>Semester:</strong> ${fcar.semester} ${fcar.year} |
                                        <strong>Status:</strong>
                                        <span class="status-badge status-${fn:toLowerCase(fcar.status)}">${fcar.status}</span>
                                    </p>
                                </div>
                                <div>
                                    <button class="btn toggle-details" onclick="toggleDetails('fcar-${fcar.fcarId}')">Show Details</button>
                                    <c:if test="${user.userId == fcar.instructorId || user.roleId == 1}">
                                        <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${fcar.fcarId}" class="btn">Edit FCAR</a>
                                    </c:if>
                                </div>
                            </div>
                            <div id="fcar-${fcar.fcarId}" class="fcar-details" style="display:none;">
                                <div class="fcar-section">
                                    <h4>Actions</h4>
                                    <c:if test="${user.userId == fcar.instructorId || user.roleId == 1}">
                                        <p><strong>Edit Status:</strong>
                                            <c:choose>
                                                <c:when test="${fcar.status == 'Draft' || user.roleId == 1}">Available</c:when>
                                                <c:otherwise>Not available for ${fcar.status} FCARs</c:otherwise>
                                            </c:choose>
                                        </p>
                                    </c:if>
                                    <c:if test="${user.roleId == 1 && fcar.status == 'Submitted'}">
                                        <p><strong>Admin Actions:</strong>
                                            <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${fcar.fcarId}" class="btn">Edit FCAR</a>
                                            Approval pending
                                        </p>
                                    </c:if>
                                </div>
                                <div class="fcar-section">
                                    <h4>FCAR Information</h4>
                                    <p><strong>Course:</strong> ${fcar.courseCode}</p>
                                    <p><strong>Instructor:</strong>
                                                <%
                                            // Get the instructor for this FCAR in the details section
                                            FCAR detailsFcar = (FCAR)pageContext.getAttribute("fcar");
                                            User detailsInstructor = DisplaySystemController.getInstance().getUser(detailsFcar.getInstructorId());
                                            if (detailsInstructor != null) {
                                                out.print(detailsInstructor.getLastName());
                                            } else {
                                                out.print("ID: " + detailsFcar.getInstructorId());
                                            }
                                        %>
                                    <p><strong>Semester/Year:</strong> ${fcar.semester} ${fcar.year}</p>
                                    <p><strong>Status:</strong> <span class="status-badge status-${fn:toLowerCase(fcar.status)}">${fcar.status}</span></p>
                                    <p><strong>Created:</strong> ${fcar.createdAt}</p>
                                    <c:if test="${fcar.updatedAt != null && not empty fcar.updatedAt}"><p><strong>Updated:</strong> ${fcar.updatedAt}</p></c:if>
                                </div>
                                <c:if test="${selectedFCAR != null && selectedFCAR.outcomeId > 0}">
                                    <div class="fcar-section">
                                        <h4>Student Learning Outcomes</h4>

                                        <c:set var="outcome" value="${outcomeMap[selectedFCAR.outcomeId]}" />
                                        <p><strong>Outcome:</strong>
                                            <c:choose>
                                                <c:when test="${outcome != null && not empty outcome}">
                                                    ${outcome.outcomeNum}: ${outcome.description}
                                                </c:when>
                                                <c:otherwise>
                                                    Outcome ID: ${selectedFCAR.outcomeId}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>

                                        <c:set var="indicators" value="${indicatorMap[selectedFCAR.outcomeId]}" />
                                        <c:set var="indicatorFound" value="false" />

                                        <c:if test="${not empty indicators}">
                                            <c:forEach var="indicator" items="${indicators}">
                                                <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                                    <c:set var="indicatorFound" value="true" />
                                                    <p><strong>Indicator:</strong> ${selectedFCAR.outcomeId}.${indicator.number}: ${indicator.description}</p>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test="${not indicatorFound}">
                                            <p><strong>Indicator ID:</strong> ${selectedFCAR.indicatorId}</p>
                                        </c:if>
                                    </div>
                                </c:if>

                                <c:if test="${fcar != null && fcar.outcomeId > 0}">
                                    <div class="fcar-section">
                                        <h4>Student Learning Outcomes</h4>

                                        <c:set var="outcome" value="${outcomeMap[fcar.outcomeId]}" />
                                        <p><strong>Outcome:</strong>
                                            <c:choose>
                                                <c:when test="${outcome != null && not empty outcome}">
                                                    ${outcome.outcomeNum}: ${outcome.description}
                                                </c:when>
                                                <c:otherwise>
                                                    Outcome ID: ${fcar.outcomeId}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>

                                        <c:set var="indicators" value="${indicatorMap[fcar.outcomeId]}" />
                                        <c:set var="indicatorFound" value="false" />

                                        <c:if test="${not empty indicators}">
                                            <c:forEach var="indicator" items="${indicators}">
                                                <c:if test="${indicator.indicatorId == fcar.indicatorId}">
                                                    <c:set var="indicatorFound" value="true" />
                                                    <p><strong>Indicator:</strong> ${fcar.outcomeId}.${indicator.number}: ${indicator.description}</p>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test="${not indicatorFound}">
                                            <p><strong>Indicator ID:</strong> ${fcar.indicatorId}</p>
                                        </c:if>
                                    </div>
                                </c:if>
                                <c:if test="${fcar != null && fcar.assessmentMethods != null && not empty fcar.assessmentMethods}">
                                    <div class="fcar-section">
                                        <h4>Assessment Methods</h4>

                                            <%-- Display Assessment Description and Work Used in a structured way --%>
                                        <div class="assessment-details">
                                            <c:if test="${fcar.assessmentMethods['assessmentDescription'] != null}">
                                                <div class="form-group">
                                                    <strong>Assessment Description:</strong>
                                                    <div class="content-box">${fcar.assessmentMethods['assessmentDescription']}</div>
                                                </div>
                                            </c:if>

                                            <c:if test="${fcar.assessmentMethods['workUsed'] != null}">
                                                <div class="form-group">
                                                    <strong>Work Used:</strong>
                                                    <div class="content-box">${fcar.assessmentMethods['workUsed']}</div>
                                                </div>
                                            </c:if>

                                            <c:if test="${fcar.assessmentMethods['targetGoal'] != null}">
                                                <div class="form-group">
                                                    <strong>Target Goal:</strong>
                                                    <div class="content-box">${fcar.assessmentMethods['targetGoal']}%</div>
                                                </div>
                                            </c:if>
                                        </div>

                                            <%-- Display selected outcomes and indicators if available --%>
                                        <c:if test="${fcar.assessmentMethods['selectedOutcomes'] != null}">
                                            <div class="form-group">
                                                <strong>Selected Outcomes and Indicators:</strong>
                                                <div class="content-box">
                                                    <c:set var="outcomeIds" value="${fn:split(fcar.assessmentMethods['selectedOutcomes'], ',')}" />
                                                    <table class="data-table">
                                                        <thead>
                                                        <tr>
                                                            <th>Outcome</th>
                                                            <th>Indicators</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <c:forEach var="outcomeId" items="${outcomeIds}">
                                                            <tr>
                                                                <td>
                                                                        <%-- Try to get outcome description from outcomeMap --%>
                                                                    <c:choose>
                                                                        <c:when test="${outcomeMap != null && outcomeMap[outcomeId] != null}">
                                                                            Outcome ${outcomeId}: ${outcomeMap[outcomeId].description}
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            Outcome ${outcomeId}
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                        <%-- Look for indicators related to this outcome --%>
                                                                    <c:set var="hasIndicators" value="false" />
                                                                    <c:forEach var="entry" items="${fcar.assessmentMethods}">
                                                                        <c:if test="${fn:startsWith(entry.key, 'indicator_'.concat(outcomeId))}">
                                                                            <c:set var="hasIndicators" value="true" />
                                                                            <div>
                                                                                    <%-- Extract indicator number from the key --%>
                                                                                <c:set var="indicatorKey" value="${entry.key}" />
                                                                                <c:set var="indicatorNum" value="${fn:substringAfter(indicatorKey, 'indicator_'.concat(outcomeId).concat('.'))}" />
                                                                                <c:choose>
                                                                                    <c:when test="${indicatorMap != null && indicatorMap[outcomeId] != null}">
                                                                                        <c:forEach var="indicator" items="${indicatorMap[outcomeId]}">
                                                                                            <c:if test="${indicator.number == indicatorNum}">
                                                                                                ${outcomeId}.${indicatorNum}: ${indicator.description}
                                                                                            </c:if>
                                                                                        </c:forEach>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        Indicator ${outcomeId}.${indicatorNum}
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </div>
                                                                        </c:if>
                                                                    </c:forEach>
                                                                    <c:if test="${!hasIndicators}">
                                                                        No specific indicators selected
                                                                    </c:if>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </c:if>

                                            <%-- Display other assessment method entries that don't fit into the categories above --%>
                                        <c:set var="hasOtherEntries" value="false" />
                                        <c:forEach var="method" items="${fcar.assessmentMethods}">
                                            <c:if test="${!fn:startsWith(method.key, 'level') &&
                                            method.key != 'assessmentDescription' &&
                                            method.key != 'workUsed' &&
                                            method.key != 'targetGoal' &&
                                            method.key != 'selectedOutcomes' &&
                                            !fn:startsWith(method.key, 'indicator_')}">
                                                <c:set var="hasOtherEntries" value="true" />
                                            </c:if>
                                        </c:forEach>

                                        <c:if test="${hasOtherEntries}">
                                            <div class="form-group">
                                                <strong>Other Assessment Details:</strong>
                                                <div class="content-box">
                                                    <c:forEach var="method" items="${fcar.assessmentMethods}">
                                                        <c:if test="${!fn:startsWith(method.key, 'level') &&
                                                        method.key != 'assessmentDescription' &&
                                                        method.key != 'workUsed' &&
                                                        method.key != 'targetGoal' &&
                                                        method.key != 'selectedOutcomes' &&
                                                        !fn:startsWith(method.key, 'indicator_')}">
                                                            <p><strong>${fn:replace(method.key, '_', ' ')}:</strong> ${method.value}</p>
                                                        </c:if>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                        </c:if>

                                            <%-- Now handle the level entries in a structured way --%>
                                        <c:if test="${fcar.assessmentMethods['level1'] != null || fcar.assessmentMethods['level2'] != null || fcar.assessmentMethods['level3'] != null}">
                                            <div class="form-group">
                                                <strong>Achievement Levels:</strong>
                                                <div class="content-box">
                                                    <table class="achievement-table">
                                                        <thead>
                                                        <tr>
                                                            <th>Exceeds Expectations</th>
                                                            <th>Meets Expectations</th>
                                                            <th>Below Expectations</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr>
                                                            <td>${fcar.assessmentMethods['level3'] != null ? fcar.assessmentMethods['level3'] : '0'}</td>
                                                            <td>${fcar.assessmentMethods['level2'] != null ? fcar.assessmentMethods['level2'] : '0'}</td>
                                                            <td>${fcar.assessmentMethods['level1'] != null ? fcar.assessmentMethods['level1'] : '0'}</td>
                                                        </tr>
                                                        </tbody>
                                                    </table>

                                                        <%-- Calculate total and percentages --%>
                                                    <c:set var="level1" value="${fcar.assessmentMethods['level1'] != null ? fcar.assessmentMethods['level1'] : 0}" />
                                                    <c:set var="level2" value="${fcar.assessmentMethods['level2'] != null ? fcar.assessmentMethods['level2'] : 0}" />
                                                    <c:set var="level3" value="${fcar.assessmentMethods['level3'] != null ? fcar.assessmentMethods['level3'] : 0}" />

                                                    <c:set var="totalStudents" value="${level1 + level2 + level3}" />
                                                    <c:set var="studentsMetTarget" value="${level2 + level3}" />
                                                    <c:set var="targetGoal" value="${fcar.assessmentMethods['targetGoal'] != null ? fcar.assessmentMethods['targetGoal'] : 70}" />

                                                    <c:if test="${totalStudents > 0}">
                                                        <div class="results-summary">
                                                            <p>Total Students: ${totalStudents}</p>
                                                            <p>Students Meeting or Exceeding Expectations: ${studentsMetTarget} (${Math.round(studentsMetTarget * 100 / totalStudents)}%)</p>
                                                            <p>Target Goal: ${targetGoal}% of students meet or exceed expectations</p>
                                                            <p>
                                                                <strong>Target Met: </strong>
                                                                <span class="${(studentsMetTarget * 100 / totalStudents) >= targetGoal ? 'status-met' : 'status-not-met'}">
                                                                        ${(studentsMetTarget * 100 / totalStudents) >= targetGoal ? 'Yes' : 'No'}
                                                                </span>
                                                            </p>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>
                                </c:if>
                                <c:if test="${fcar != null && fcar.studentOutcomes != null && not empty fcar.studentOutcomes}">
                                    <div class="fcar-section">
                                        <h4>Student Outcomes</h4>
                                        <c:forEach var="outcome" items="${fcar.studentOutcomes}">
                                            <p><strong>${fn:replace(outcome.key, '_', ' ')}:</strong> ${outcome.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <c:if test="${fcar != null && fcar.improvementActions != null && not empty fcar.improvementActions}">
                                    <div class="fcar-section">
                                        <h4>Improvement Actions</h4>
                                        <c:forEach var="act" items="${fcar.improvementActions}">
                                            <p><strong>${fn:replace(act.key, '_', ' ')}:</strong> ${act.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <p class="no-data">No FCARs found. Please create a new FCAR.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="action-buttons">
        <a href="${dashboardUrl}" class="btn" style="margin-right: 10px;">Back to Dashboard</a>
        <c:set var="paramFcarId" value="${param.fcarId}" />
        <c:if test="${(paramFcarId != null && paramFcarId != '') || (fcarId != null && fcarId != '')}">
            <c:set var="editFcarId" value="${(paramFcarId != null && paramFcarId != '') ? paramFcarId : fcarId}" />
            <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${editFcarId}" class="btn">Edit FCAR</a>
        </c:if>
    </div>

    <!-- Import functionality has been moved to the Settings page -->

    <!-- JavaScript for expanding/collapsing FCAR details -->
    <script>
        function toggleDetails(id) {
            const details = document.getElementById(id);
            const button = event.currentTarget;

            if (details.style.display === "none") {
                details.style.display = "block";
                button.textContent = "Hide Details";
            } else {
                details.style.display = "none";
                button.textContent = "Show Details";
            }
        }

        // Function to toggle indicators when an outcome is deselected
        function toggleIndicators(outcomeId, checked) {
            const indicatorsDiv = document.getElementById(`indicators_${outcomeId}`);
            if (indicatorsDiv) {
                indicatorsDiv.style.display = checked ? 'block' : 'none';

                // If outcome is unchecked, uncheck all its indicators
                if (!checked) {
                    const indicators = document.querySelectorAll(`input[id^="indicator_${outcomeId}_"]`);
                    indicators.forEach(indicator => {
                        indicator.checked = false;
                    });
                }
            }
        }

        // Add event listeners to outcome checkboxes
        document.addEventListener('DOMContentLoaded', function() {
            const outcomeCheckboxes = document.querySelectorAll('input[type="checkbox"][id^="outcome_"]');
            outcomeCheckboxes.forEach(checkbox => {
                checkbox.addEventListener('change', function() {
                    toggleIndicators(this.value, this.checked);
                });
            });
        });
    </script>
</div>

    <script>
        function toggleDetails(id) {
            const el = document.getElementById(id);
            const btn = event.currentTarget;
            if (el.style.display === 'none') {
                el.style.display = 'block'; btn.textContent = 'Hide Details';
            } else {
                el.style.display = 'none'; btn.textContent = 'Show Details';
            }
        }

        // Import functionality has been moved to the Settings page
    </script>
</body>
</html>
